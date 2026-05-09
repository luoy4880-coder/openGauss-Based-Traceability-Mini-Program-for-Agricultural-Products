import copy
import re
import zipfile
from dataclasses import dataclass
from pathlib import Path
import xml.etree.ElementTree as ET


W_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
NS = {"w": W_NS}


@dataclass(frozen=True)
class StyleIds:
    heading1: str | None
    heading2: str | None
    heading3: str | None


def load_style_ids(template_docx: Path) -> StyleIds:
    with zipfile.ZipFile(template_docx) as z:
        try:
            styles_xml = z.read("word/styles.xml")
        except KeyError:
            return StyleIds(None, None, None)

    root = ET.fromstring(styles_xml)

    # style name variants found in Word/WPS: "heading 1", "Heading 1", "标题 1"
    wanted = {
        1: {"heading 1", "Heading 1", "标题 1"},
        2: {"heading 2", "Heading 2", "标题 2"},
        3: {"heading 3", "Heading 3", "标题 3"},
    }
    found: dict[int, str] = {}

    for style in root.findall(".//w:style", NS):
        style_id = style.attrib.get(f"{{{W_NS}}}styleId")
        if not style_id:
            continue
        name_el = style.find("w:name", NS)
        if name_el is None:
            continue
        style_name = name_el.attrib.get(f"{{{W_NS}}}val")
        if not style_name:
            continue
        for level, names in wanted.items():
            if style_name in names:
                found[level] = style_id

    return StyleIds(found.get(1), found.get(2), found.get(3))


def find_sectpr(body: ET.Element) -> ET.Element | None:
    # Prefer direct child <w:sectPr> (typical). Otherwise search last occurrence.
    direct = body.findall("w:sectPr", NS)
    if direct:
        return direct[-1]
    sectprs = body.findall(".//w:sectPr", NS)
    return sectprs[-1] if sectprs else None


def make_paragraph(text: str, style_id: str | None) -> ET.Element:
    p = ET.Element(f"{{{W_NS}}}p")
    if style_id:
        ppr = ET.SubElement(p, f"{{{W_NS}}}pPr")
        pstyle = ET.SubElement(ppr, f"{{{W_NS}}}pStyle")
        pstyle.set(f"{{{W_NS}}}val", style_id)

    r = ET.SubElement(p, f"{{{W_NS}}}r")
    t = ET.SubElement(r, f"{{{W_NS}}}t")
    if text.startswith(" ") or text.endswith(" "):
        t.set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
    t.text = text
    return p


def md_to_paragraphs(md_text: str, styles: StyleIds) -> list[ET.Element]:
    paragraphs: list[ET.Element] = []
    for raw_line in md_text.splitlines():
        line = raw_line.rstrip("\n")
        if not line.strip():
            paragraphs.append(make_paragraph("", None))
            continue

        if line.startswith("# "):
            paragraphs.append(make_paragraph(line[2:].strip(), styles.heading1))
            continue
        if line.startswith("## "):
            paragraphs.append(make_paragraph(line[3:].strip(), styles.heading2))
            continue
        if line.startswith("### "):
            paragraphs.append(make_paragraph(line[4:].strip(), styles.heading3))
            continue

        # Remove trailing two spaces Markdown line break marker
        cleaned = re.sub(r"\s\s+$", "", line)
        paragraphs.append(make_paragraph(cleaned, None))
    return paragraphs


def generate_docx(template_docx: Path, md_path: Path, out_docx: Path) -> None:
    md_text = md_path.read_text(encoding="utf-8")
    styles = load_style_ids(template_docx)

    with zipfile.ZipFile(template_docx) as zin:
        with zipfile.ZipFile(out_docx, "w", compression=zipfile.ZIP_DEFLATED) as zout:
            # Read/patch document.xml
            doc_xml = zin.read("word/document.xml")
            doc_root = ET.fromstring(doc_xml)
            body = doc_root.find("w:body", NS)
            if body is None:
                raise RuntimeError("Invalid docx: missing word/document.xml w:body")

            sectpr = find_sectpr(body)
            sectpr_copy = copy.deepcopy(sectpr) if sectpr is not None else None

            # Clear existing body content
            for child in list(body):
                body.remove(child)

            # Build new body
            for p in md_to_paragraphs(md_text, styles):
                body.append(p)

            if sectpr_copy is not None:
                body.append(sectpr_copy)

            patched_doc_xml = ET.tostring(doc_root, encoding="utf-8", xml_declaration=True)

            for item in zin.infolist():
                if item.filename == "word/document.xml":
                    zout.writestr(item, patched_doc_xml)
                else:
                    zout.writestr(item, zin.read(item.filename))


def main() -> None:
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("--template", required=True, help="template .docx path")
    parser.add_argument("--md", required=True, help="markdown content path (utf-8)")
    parser.add_argument("--out", required=True, help="output .docx path")
    args = parser.parse_args()

    template_docx = Path(args.template)
    md_path = Path(args.md)
    out_docx = Path(args.out)

    generate_docx(template_docx, md_path, out_docx)


if __name__ == "__main__":
    main()


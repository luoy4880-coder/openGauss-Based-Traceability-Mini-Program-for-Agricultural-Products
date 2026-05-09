import sys, re, zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

DOC_NS = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}


def load_style_map(docx_path: Path) -> dict[str, str]:
    """Return mapping: styleId -> human-readable style name."""
    style_map: dict[str, str] = {}
    with zipfile.ZipFile(docx_path) as z:
        try:
            styles_xml = z.read('word/styles.xml')
        except KeyError:
            return style_map

    root = ET.fromstring(styles_xml)
    for style in root.findall('.//w:style', DOC_NS):
        style_id = style.attrib.get('{%s}styleId' % DOC_NS['w'])
        if not style_id:
            continue
        name = style.find('w:name', DOC_NS)
        if name is None:
            continue
        style_name = name.attrib.get('{%s}val' % DOC_NS['w'])
        if style_name:
            style_map[style_id] = style_name
    return style_map


def iter_paragraphs(docx_path: Path):
    with zipfile.ZipFile(docx_path) as z:
        xml_bytes = z.read('word/document.xml')
    root = ET.fromstring(xml_bytes)
    for wp in root.findall('.//w:p', DOC_NS):
        pstyle = None
        ppr = wp.find('w:pPr', DOC_NS)
        if ppr is not None:
            ps = ppr.find('w:pStyle', DOC_NS)
            if ps is not None:
                pstyle = ps.attrib.get('{%s}val' % DOC_NS['w'])

        texts = [t.text for t in wp.findall('.//w:t', DOC_NS) if t.text]
        if not texts:
            continue

        txt = ''.join(texts)
        txt = re.sub(r'\s+', ' ', txt).strip()
        if not txt:
            continue

        # Heuristic filters for embedded binary/base64-ish garbage
        if len(txt) > 2000:
            continue
        if len(txt) > 400 and ('UEsDB' in txt or 'R0lGOD' in txt or 'iVBOR' in txt):
            continue

        yield pstyle, txt


def main():
    if len(sys.argv) < 2:
        raise SystemExit('usage: extract_docx_outline.py <path.docx>')

    docx_path = Path(sys.argv[1])
    if not docx_path.exists():
        raise SystemExit(f'not found: {docx_path}')

    style_map = load_style_map(docx_path)
    paras = list(iter_paragraphs(docx_path))

    limit = 250
    for i, (style, text) in enumerate(paras[:limit], start=1):
        if style and style in style_map:
            style_label = f'{style_map[style]}({style})'
        else:
            style_label = style or '-'
        print(f'{i:03d}\t{style_label}\t{text}')

    print('---')
    print('PARA_COUNT', len(paras))


if __name__ == '__main__':
    main()

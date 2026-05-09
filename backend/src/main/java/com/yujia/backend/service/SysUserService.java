package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.auth.AuthUser;
import com.yujia.backend.common.auth.PasswordUtil;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.auth.BootstrapAdminRequest;
import com.yujia.backend.dto.user.UserCreateRequest;
import com.yujia.backend.dto.user.UserPasswordUpdateRequest;
import com.yujia.backend.dto.user.UserUpdateRequest;
import com.yujia.backend.entity.SysUser;
import com.yujia.backend.mapper.SysUserMapper;
import com.yujia.backend.mapper.SysUserRoleMapper;
import com.yujia.backend.vo.RoleVO;
import com.yujia.backend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleService sysRoleService;
    private final CompanyService companyService;
    private final CompanyScopeService companyScopeService;
    private final AuthPermissionService authPermissionService;

    public SysUser findByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    public SysUser findByOpenId(String openid) {
        return sysUserMapper.selectByOpenId(openid);
    }

    public List<SysUser> listActiveStaffEntities() {
        return sysUserMapper.selectActiveStaff();
    }

    public List<SysUser> listActiveStaffEntitiesByCompanyId(Long companyId) {
        if (companyId == null) {
            return listActiveStaffEntities();
        }
        return sysUserMapper.selectActiveStaffByCompanyId(companyId);
    }

    public boolean isActiveStaff(Long userId) {
        if (userId == null) {
            return false;
        }
        return sysUserMapper.countActiveStaffById(userId) > 0;
    }

    @Transactional
    public SysUser createByOpenId(String openid) {
        SysUser user = new SysUser();
        user.setUsername("wx_" + openid.substring(openid.length() - 8));
        user.setRealName("微信用户");
        user.setOpenid(openid);
        user.setCompanyId(null);
        user.setStatus(1);
        user.setPassword(PasswordUtil.encode("WX_BIND_" + UUID.randomUUID()));
        sysUserMapper.insert(user);
        assignRoleByCode(user.getId(), "USER");
        return user;
    }

    @Transactional
    public SysUser register(String username, String password, String realName, String phone, String companyName) {
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        user.setRealName(realName == null || realName.isBlank() ? username : realName);
        user.setPhone(phone);
        user.setCompanyId(companyService.resolveOrCreateCompanyId(companyName));
        user.setStatus(1);
        sysUserMapper.insert(user);
        assignRoleByCode(user.getId(), "USER");
        return sysUserMapper.selectById(user.getId());
    }

    @Transactional
    public SysUser bindAccount(Long userId, String username, String password, String realName, String phone) {
        SysUser existing = sysUserMapper.selectByUsername(username);
        if (existing != null && !existing.getId().equals(userId)) {
            throw new BusinessException("用户名已存在");
        }

        SysUser user = detailEntity(userId);
        if (user.getOpenid() == null || user.getOpenid().isBlank()) {
            throw new BusinessException(400, "该账号不是微信登录账号，不能绑定");
        }

        boolean isWeChatGeneratedUsername = user.getUsername() != null && user.getUsername().startsWith("wx_");
        if (!isWeChatGeneratedUsername) {
            throw new BusinessException(400, "该账号已绑定过，不能重复绑定");
        }

        user.setUsername(username);
        user.setPassword(PasswordUtil.encode(password));
        if (realName != null && !realName.isBlank()) {
            user.setRealName(realName);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        sysUserMapper.updateAuthById(user);
        return detailEntity(userId);
    }

    public UserVO detail(Long id) {
        return toVO(detailEntity(id));
    }

    public PageResponse<UserVO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        long total = sysUserMapper.countPage(companyId, keyword, status);
        List<UserVO> records = sysUserMapper.selectPage(
                companyId,
                keyword,
                status,
                (long) (safePageNum - 1) * safePageSize,
                safePageSize
        ).stream().map(this::toVO).toList();
        return PageResponse.<UserVO>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    @Transactional
    public UserVO create(UserCreateRequest request) {
        if (sysUserMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        sysRoleService.validateRoleIds(request.getRoleIds());
        Long targetCompanyId = normalizeManageableCompanyId(request.getCompanyId());
        companyService.detailEntity(targetCompanyId);

        SysUser sysUser = new SysUser();
        sysUser.setUsername(request.getUsername());
        sysUser.setPassword(PasswordUtil.encode(request.getPassword()));
        sysUser.setRealName(request.getRealName());
        sysUser.setPhone(request.getPhone());
        sysUser.setCompanyId(targetCompanyId);
        sysUser.setStatus(request.getStatus());
        sysUserMapper.insert(sysUser);
        sysUserRoleMapper.insertBatch(sysUser.getId(), request.getRoleIds());
        return detail(sysUser.getId());
    }

    @Transactional
    public UserVO update(Long id, UserUpdateRequest request) {
        SysUser sysUser = detailEntity(id);
        sysRoleService.validateRoleIds(request.getRoleIds());
        Long targetCompanyId = normalizeManageableCompanyId(request.getCompanyId());
        companyService.detailEntity(targetCompanyId);

        sysUser.setRealName(request.getRealName());
        sysUser.setPhone(request.getPhone());
        sysUser.setCompanyId(targetCompanyId);
        sysUser.setStatus(request.getStatus());
        sysUserMapper.updateById(sysUser);
        sysUserRoleMapper.deleteByUserId(id);
        sysUserRoleMapper.insertBatch(id, request.getRoleIds());
        return detail(id);
    }

    public void updatePassword(Long id, UserPasswordUpdateRequest request) {
        detailEntity(id);
        sysUserMapper.updatePassword(id, PasswordUtil.encode(request.getNewPassword()));
    }

    public UserVO updateProfile(Long id, String realName, String phone) {
        SysUser sysUser = detailEntity(id);
        sysUser.setRealName(realName);
        sysUser.setPhone(phone);
        sysUserMapper.updateProfileById(sysUser);
        return detail(id);
    }

    @Transactional
    public void delete(Long id) {
        detailEntity(id);
        sysUserRoleMapper.deleteByUserId(id);
        sysUserMapper.deleteById(id);
    }

    @Transactional
    public UserVO bootstrapAdmin(BootstrapAdminRequest request) {
        if (sysUserMapper.countAll() > 0) {
            throw new BusinessException("系统已初始化管理员，不能重复初始化");
        }

        List<RoleVO> roles = sysRoleService.list();
        Long adminRoleId = roles.stream()
                .filter(role -> "ADMIN".equalsIgnoreCase(role.getRoleCode()))
                .map(RoleVO::getId)
                .findFirst()
                .orElseThrow(() -> new BusinessException("缺少ADMIN角色，请先初始化角色数据"));

        SysUser sysUser = new SysUser();
        sysUser.setUsername(request.getUsername());
        sysUser.setPassword(PasswordUtil.encode(request.getPassword()));
        sysUser.setRealName(request.getRealName());
        sysUser.setPhone(request.getPhone());
        sysUser.setCompanyId(companyService.resolveOrCreateCompanyId("默认公司"));
        sysUser.setStatus(1);
        sysUserMapper.insert(sysUser);
        sysUserRoleMapper.insertBatch(sysUser.getId(), List.of(adminRoleId));
        return detail(sysUser.getId());
    }

    public AuthUser loadAuthUser(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            return null;
        }
        List<String> roleCodes = sysRoleService.listByUserId(userId).stream().map(RoleVO::getRoleCode).toList();
        return AuthUser.builder()
                .userId(sysUser.getId())
                .username(sysUser.getUsername())
                .realName(sysUser.getRealName())
                .companyId(sysUser.getCompanyId())
                .companyName(sysUser.getCompanyName())
                .roleCodes(roleCodes)
                .build();
    }

    private SysUser detailEntity(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            throw new BusinessException(404, "用户不存在");
        }
        var currentUser = AuthContext.get();
        if (currentUser != null && currentUser.getUserId() != null && currentUser.getUserId().equals(id)) {
            return sysUser;
        }
        if (!authPermissionService.isAdmin()) {
            companyScopeService.assertAccessibleCompany(sysUser.getCompanyId());
            if (sysRoleService.listByUserId(id).stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getRoleCode()))) {
                throw new BusinessException(403, "无权操作管理员账号");
            }
        }
        return sysUser;
    }

    private Long normalizeManageableCompanyId(Long requestCompanyId) {
        if (authPermissionService.isAdmin()) {
            if (requestCompanyId == null) {
                throw new BusinessException(400, "公司不能为空");
            }
            return requestCompanyId;
        }
        Long currentCompanyId = companyScopeService.requireCurrentCompanyId();
        if (requestCompanyId != null && !currentCompanyId.equals(requestCompanyId)) {
            throw new BusinessException(403, "无权操作其他公司的用户");
        }
        return currentCompanyId;
    }

    private void assignRoleByCode(Long userId, String roleCode) {
        Long userRoleId = sysRoleService.findRoleIdByCode(roleCode);
        if (userRoleId == null) {
            return;
        }
        sysUserRoleMapper.insertBatch(userId, List.of(userRoleId));
    }

    private UserVO toVO(SysUser sysUser) {
        UserVO userVO = new UserVO();
        userVO.setId(sysUser.getId());
        userVO.setUsername(sysUser.getUsername());
        userVO.setRealName(sysUser.getRealName());
        userVO.setPhone(sysUser.getPhone());
        userVO.setCompanyId(sysUser.getCompanyId());
        userVO.setCompanyName(sysUser.getCompanyName());
        userVO.setStatus(sysUser.getStatus());
        userVO.setCreatedAt(sysUser.getCreatedAt());
        userVO.setUpdatedAt(sysUser.getUpdatedAt());
        userVO.setRoles(sysRoleService.listByUserId(sysUser.getId()));
        return userVO;
    }
}

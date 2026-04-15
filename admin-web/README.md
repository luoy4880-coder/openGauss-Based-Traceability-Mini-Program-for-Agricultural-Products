# admin-web

Vue 3 管理端项目，服务于农产品追溯系统后台。

## 当前已搭好

- Vue 3 + Vite 项目骨架
- Vue Router 路由结构
- Pinia 登录状态管理
- Axios 请求封装
- 登录页
- 管理后台主布局
- 仪表盘页面
- 基地、批次、生产记录、质检、召回、用户、个人信息页面占位骨架

## 启动步骤

在 `admin-web` 目录执行：

```powershell
npm install
npm run dev
```

默认开发地址：

```text
http://localhost:5173
```

## 后端联调说明

- 当前请求地址默认写的是 `http://localhost:8080`
- 在 [http.ts](/F:/GraduationProject-yujia/yujia-test/admin-web/src/api/http.ts) 里可修改
- 除登录、初始化管理员、健康检查、追溯公开接口外，其余 `/api/**` 都需要 `Bearer token`

## 建议联调顺序

1. 先调用 `POST /api/auth/bootstrap` 初始化管理员
2. 再用登录页调用 `POST /api/auth/login`
3. 登录后自动调 `GET /api/auth/me`
4. 仪表盘调 `GET /api/dashboard/stats`
5. 之后逐页接分页列表和表单

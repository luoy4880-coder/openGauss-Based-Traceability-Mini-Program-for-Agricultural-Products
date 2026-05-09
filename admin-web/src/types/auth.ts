export type RoleItem = {
  id: number
  roleCode: string
  roleName: string
  remark?: string
}

export type CurrentUser = {
  id: number
  username: string
  realName: string
  phone?: string
  companyId?: number
  companyName?: string
  status: number
  roles: RoleItem[]
}

export type LoginResult = {
  token: string
  userId: number
  username: string
  realName: string
  roleCodes: string[]
}

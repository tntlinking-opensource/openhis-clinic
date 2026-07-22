/**
 * 远程诊疗/院版 HIS 接口统一调用层
 *
 * 所有 apis/ 开头的外部接口通过此模块调用，避免在视图中硬编码 URL 和重复 boilerplate。
 * 调用方式：import { treatedApplyInfo, getPatient, ... } from '@/api/remoteApis'
 */
import axios from 'axios'

const APP_ID = 'Oh_Newtouch_Clinic'

/**
 * 创建远程 API 请求函数
 * @param {string} url - 接口路径 (如 'apis/RemoteTreated/TreatedApplyInfo')
 * @returns {function} 请求函数，接收 (data, organizeId, token) 参数
 */
function createRemoteApi(url) {
  return (data, organizeId, token) =>
    axios.post(url, {
      Data: data,
      OrganizeId: organizeId,
      AppId: APP_ID,
      Timestamp: new Date()
    }, {
      headers: {
        Authorization: 'Bearer ' + token
      }
    })
}

// ========== 远程诊疗 ==========

/** 会诊申请信息查询 */
export const treatedApplyInfo = createRemoteApi('apis/RemoteTreated/TreatedApplyInfo')

/** 远程会诊申请（仅转诊） */
export const remoteTreatedApplyZfOnly = createRemoteApi('apis/RemoteTreated/RemoteTreatedApplyZfOnly')

/** 医生会议申请 */
export const doctorMeetingApply = createRemoteApi('apis/RemoteTreated/DoctorMeetingApply')

/** 用户加入会议申请 */
export const userJoinMeetingApply = createRemoteApi('apis/RemoteTreated/UserJoinMeetingApply')

/** 会诊申请取消 */
export const treatedApplyCancel = createRemoteApi('apis/RemoteTreated/TreatedApplyCancel')

// ========== 患者 ==========

/** 查询患者信息 */
export const getPatient = createRemoteApi('apis/Patient/GetPatient')

/** 患者自助挂号 */
export const patientSelfReg = createRemoteApi('apis/Patient/PatientSelfReg')

/** 添加患者地址 */
export const patientAddressAdd = createRemoteApi('apis/patient/PatientAddressAdd')

/** 更新患者地址 */
export const patientAddressUpdate = createRemoteApi('apis/patient/PatientAddressUpdate')

/** 查询患者订单地址 */
export const patientOrderAddressQuery = createRemoteApi('apis/patient/PatientOrderAddressQuery')

// ========== 门诊 ==========

/** 门诊账单未缴费查询 */
export const outpBillUnpaidByMzh = createRemoteApi('apis/Outpatient/OutpBillUnpaidByMzh')

/** 门诊账单项目明细 */
export const outpBillItemDetail = createRemoteApi('apis/Outpatient/OutpBillItemDetail')

// ========== 挂号/预约 ==========

/** 获取机构列表 */
export const getOrgList = createRemoteApi('apis/SysOrg/GetOrgList')

/** 获取门诊科室 */
export const getOutpDepartment = createRemoteApi('apis/Booking/GetOutpDepartment')

/** 获取门诊医生 */
export const getOutpDoctor = createRemoteApi('apis/Booking/GetOutpDoctor')

// ========== 支付 ==========

/** 订单查询 */
export const orderQuery = createRemoteApi('apis/PayOrder/OrderQuery')

/** 创建账单订单 */
export const billOrderCreate = createRemoteApi('apis/PayOrder/BillOrderCreate')

/** 账单锁定申请 */
export const billOrderLockApply = createRemoteApi('apis/PayOrder/BillOrderLockApply')

/** 账单支付 */
export const billOrderPay = createRemoteApi('apis/PayOrder/BillOrderPay')

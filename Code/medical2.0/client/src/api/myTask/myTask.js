import request from '@/utils/request'

export const getClinicOfficeById = (id) =>
    request({
        url: '/myTask/myTask/' + id,
        method: 'get'
    })



export const listMyTaskPage = (search) =>
    request({
        url: '/myTask/myTask/list',
        method: 'post',
        data: search
    })

export const listMyTaskAll = (search) =>
    request({
        url: '/myTask/myTask/listAll',
        method: 'post',
        data: search
    })


export const saveMyTask = (myTask) => 
    request({
        url: '/myTask/myTask//save',
        method: 'post',
        data: myTask
    })
  
export const updateMyTask = (myTask) =>
    request({
        url: '/myTask/myTask/update',
        method: 'post',
        data: myTask
    })
    
export const bulkInsertClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkInsert',
        method: 'post',
        data: clinicOffices
    })
    
export const bulkUpdateClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkUpdate',
        method: 'post',
        data: clinicOffices
    })

export const bulkDeleteClinicOffice = (clinicOffices) =>
    request({
        url: '/org/clinicOffice/bulkDelete',
        method: 'post',
        data: clinicOffices
    })
    

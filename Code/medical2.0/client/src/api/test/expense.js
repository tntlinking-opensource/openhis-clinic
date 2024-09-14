import request from '@/utils/request'

export const getExpenseById = (id) =>
    request({
        url: '/test/expense/' + id,
        method: 'get'
    })

export const listExpensePage = (search) =>
    request({
        url: '/test/expense/list',
        method: 'post',
        data: search
    })

export const listExpenseAll = (search) =>
    request({
        url: '/test/expense/listAll',
        method: 'post',
        data: search
    })    


export const saveExpense = (expense) => 
    request({
        url: '/test/expense/save',
        method: 'post',
        data: expense
    })

export const deleteExpense = (expense) =>
    request({
        url: '/test/expense/delete',
        method: 'post',
        data: expense
    })
    
export const getExpenseByTaskId = (TaskId) =>
    request({
        url: '/test/expense/task/' + TaskId,
        method: 'get'
    })

export const createExpense = (processId, formData) => 
    request({
        url: '/test/expense/' + processId + '/create',
        method: 'post',
        data: formData
    })

export const agreeExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/agree',
        method: 'post',
        data: formData
    })

export const forwardExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/forward',
        method: 'post',
        data: formData
    })


export const commissionExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/commission',
        method: 'post',
        data: formData
    })
    
export const proposeExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/propose',
        method: 'post',
        data: formData
    })    

export const backExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/back',
        method: 'post',
        data: formData
    })
export const rejectExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/reject',
        method: 'post',
        data: formData
    })

export const terminateExpense = (taskId, formData) => 
    request({
        url: '/test/expense/' + taskId + '/terminate',
        method: 'post',
        data: formData
    })

export const listBackActivity = (taskId) => 
    request({
        url: '/test/expense/' + taskId + '/listBackActivity',
        method: 'get'
    })
export const parseExtInfo = (task) => {
  try {
    task['extInfo'] = JSON.parse(task.description)
  } catch(err) {
    task['extInfo'] = {'desc': task.description}
  }

  // 日期格式化
  if(task['startTime']) {
    task['startTime'] = new Date(task['startTime']).Format('yyyy-MM-dd hh:mm:ss')
  }
  if(task['endTime']) {
    task['endTime'] = new Date(task['endTime']).Format('yyyy-MM-dd hh:mm:ss')
  }
  if(task['due']) {
    task['due'] = new Date(task['due']).Format('yyyy-MM-dd hh:mm:ss')
  }
  if(task['created']) {
    task['created'] = new Date(task['created']).Format('yyyy-MM-dd hh:mm:ss')
  }
  return task
}

export const parseExtInfoForList = (tasks) => {
  for(let idx in tasks) {
    let task = tasks[idx]
    task = parseExtInfo(task)
  }
  return tasks
}

export const frtCommentIcon = (comment) => {
  if(comment.operation == '退回'){
    return 'el-icon-d-arrow-left'
  }
  if(comment.operation == '委派') {
    return 'el-icon-refresh'
  }
  if(comment.operation == '拟办') {
    return 'el-icon-edit-outline'
  }
  if(comment.operation == '转派') {
    return 'el-icon-refresh-right'
  }
  if(comment.operation == '终止') {
    return 'el-icon-close'
  }
  if(comment.operation == '认领') {
    return 'el-icon-user-solid'
  }  
  return 'el-icon-finished'
}
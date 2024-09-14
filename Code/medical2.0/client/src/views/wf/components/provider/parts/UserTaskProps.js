'use strict';

var is = require('bpmn-js/lib/util/ModelUtil').is,
    entryFactory = require('bpmn-js-properties-panel/lib/factory/EntryFactory');

var userField = require('../factory/UserEntryFactiory');

var cmdHelper = require('bpmn-js-properties-panel/lib/helper/CmdHelper');

var userTaskConfig = require('./UserTaskConfig').UserTaskConfig

var candiateConfig = require('@/views/wf/cfg/candidate').candiateConfig
/*任务办理人指派方式*/
var autoRandomAssignee = 'com.geeke.camundaex.assign.impl.RandomAssign'   // 自动随机派发
var autoBalanceAssignee = 'com.geeke.camundaex.assign.impl.BalanceAssign'  // 自动平衡工作派发
var autoAllAssignee = 'com.geeke.camundaex.assign.impl.AllAssign'         // 自动全部派发
var manualAssignee = 'com.geeke.camundaex.assign.impl.ManulAssign'           // 人工派发
/*候选人指派方式*/
var autoAllCandidate = 'com.geeke.camundaex.assign.impl.AllCandidate'         // 全可认领
var manualCandidate = 'com.geeke.camundaex.assign.impl.ManulCandidate'           // 指定认领

/*任务办理人配置方式*/
var selectUser = 'com.geeke.camundaex.candidate.impl.CfgCandidate'   // 自由选择客户模式
var startUser = 'com.geeke.camundaex.candidate.impl.StarterCandidate'     // 获取流程发起人模式

function isMultiInstanceSupported(element) {
  return element.businessObject.loopCharacteristics
}

function getParame(element) {
  for (var val in candiateConfig) {
    var item = candiateConfig[val]
    if(element.businessObject.candidateUsers == item.value) {
      return item.param
    }
  }
}

module.exports = function(group, element, translate) {
  if (is(element, 'camunda:Assignable')) {
    group.entries.push(entryFactory.selectBox({
      id: 'candidateGroups',
      label: '任务派发方式',
      modelProperty: 'candidateGroups',
      emptyParameter: false,
      selectOptions: function(element, node) {
        let options = []
        if(isMultiInstanceSupported(element)) {
          options.push({ value: manualAssignee, name: '人工分配' })
          options.push({ value: autoAllAssignee, name: '全部' })
        } else {
          options.push({ value: manualAssignee, name: '人工分配' })
          options.push({ value: autoRandomAssignee, name: '随机分配' })
          options.push({ value: autoBalanceAssignee, name: '负载平衡分配' })

          options.push({ value: autoAllCandidate, name: '全可认领' })
          options.push({ value: manualCandidate, name: '指派认领' })
        }
        return options
      }
    })); 

    group.entries.push(entryFactory.selectBox({
      id: 'candidateUsers',
      label: '办理人',
      modelProperty: 'candidateUsers',
      emptyParameter: false,
      selectOptions: function(element, node) {
        var options = []
        options.push({ value: selectUser, name: '选择人员' })
        options.push({ value: startUser, name: '任务发起人' })
        for (var val in candiateConfig) {
            var item = candiateConfig[val]

            options.push({ value: item.value, name: item.label })
        }

        return options
      }
    }));


    // 根据candidateUsers 值显示不同的参数输入框，注意id不要相同
  if(element.businessObject.candidateUsers == startUser) {
  } else if(element.businessObject.candidateUsers == selectUser) {
    group.entries.push(userField({
      id : 'assignee-select',         // id不重复，可以覆盖
      label : '请选择办理候选人',
      modelProperty : 'assignee',
      doSelectUser: userTaskConfig.doSelectUser,   // 用户点击选择用户按钮回调事件
      get: function(element, node) {
        var groups = ''
        if(element.businessObject.assignee) {
          var candidateGroups = JSON.parse(element.businessObject.assignee)
          for(var idx in candidateGroups) {
            if(idx == 0) {
              groups = candidateGroups[idx].name
            }else{
              groups = groups + ',' + candidateGroups[idx].name
            }
          }
        }
        return {
          'assignee': groups
        }
      }
    }));
  } else {
    var param = getParame(element);
    if(param) {
      group.entries.push(entryFactory.textField({
        id : 'assignee',
        label : param.label,
        modelProperty : 'assignee'
      }));  
    }
  } 


    // Due Date
    group.entries.push(entryFactory.textField({
      id : 'dueDate',
      description : translate('The due date as an EL expression (e.g. ${someDate} or an ISO date (e.g. 2015-06-26T09:54:00)'),
      label : translate('Due Date'),
      modelProperty : 'dueDate'
    }));

    // FollowUp Date
    group.entries.push(entryFactory.textField({
      id : 'followUpDate',
      description : translate('The follow up date as an EL expression (e.g. ${someDate} or an ' +
                    'ISO date (e.g. 2015-06-26T09:54:00)'),
      label : translate('Follow Up Date'),
      modelProperty : 'followUpDate'
    }));

    // priority
    group.entries.push(entryFactory.textField({
      id : 'priority',
      label : translate('Priority'),
      modelProperty : 'priority'
    }));
  }
};

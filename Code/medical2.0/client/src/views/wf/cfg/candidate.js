const candiateConfig = [
	{
        value: 'com.geeke.wf.candidate.CandidateByRole',  // 实现接口com.geeke.camundaex.candidate.ICandidate
        label: ' 角色用户',    // 标题
        param: {
            label: '输入角色'	// 参数框标题
        }
    },
    { 
        value: 'com.geeke.wf.candidate.StarterDepartManager',  // 实现接口com.geeke.camundaex.candidate.ICandidate
        label: ' 发起人部门经理'
    },
    {
      value: 'com.geeke.wf.candidate.StarterDirector',  // 实现接口com.geeke.camundaex.candidate.ICandidate
      label: ' 发起人部门总监'
    },
    {
      value: 'com.geeke.wf.candidate.StarterGeneralManager',  // 实现接口com.geeke.camundaex.candidate.ICandidate
      label: ' 发起人公司总经理'
    },
    {
      value: 'com.geeke.wf.candidate.StarterChairman',  // 实现接口com.geeke.camundaex.candidate.ICandidate
      label: ' 发起人公司董事长'
    }
    // 按上述的格式添加自定义的候选人服务

]

export { candiateConfig }

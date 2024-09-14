<template>
  <el-dialog title='数据权限' :visible.sync='visible' :close-on-click-modal='false' :append-to-body='true' :lock-scroll='true' v-loading='loading' width='80%'>
    <el-form ref='metaForm' :model="metaFormModel" label-width='1px' label-position='right' class='edit-form'>
      <el-row v-for='(metaData, idxMeta) in metaFormModel.metaDatas' :key='idxMeta'>
        <el-card  shadow='hover'>
          <div slot='header' class='clearfix'>
            <span>
              元数据：{{metaData.name}}
            </span>
          </div>
          <ConditionPanel ref='conditionPanel' v-model='metaData.dataRules' :tableId='metaData.id' :schemeId='metaData.schemeId' :routerId='metaData.routerId'></ConditionPanel>
        </el-card>
        <span>&nbsp;</span>
      </el-row>
    </el-form>
    <span slot='footer' class='dialog-footer'>
      <el-button type='primary' :plain=true @click='onOk()'>确 定</el-button> 
      <el-button :plain=true @click='onDialogClose()'>取 消</el-button>
    </span>    
  </el-dialog>
</template>
<script>
import ConditionPanel from '@/views/components/conditonPanel'
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'data-permission',
  components: {
    ConditionPanel
  },  
  data() {
    return {
      metaFormModel: {
        metaDatas: []    // 待选节点列表
      },
      visible: false,
      roleId: null,       // 当前配置的角色Id
      routerId: null     // 当前配置的路由Id
    }    
  },
  props: {
    //接受外部v-model传入的值
    value: {
      type: Array,
      required: true
    }
  }, 
  methods: {
    onOk() {
      let roleId = this.roleId
      let routerId = this.routerId
      let dataRules = this.value
      this.$refs['metaForm'].validate(valid => {
        if (valid) {
          // 移除元数据的权限
          this.metaFormModel.metaDatas.forEach((item)=>{
            dataRules = dataRules.filter((ruel) => {
              return !(ruel.metaId == item.id && ruel.routerId == routerId)
            })
          })
          // 添加新的数据权限
          this.metaFormModel.metaDatas.forEach((item)=>{
            // 仅保存配置了有效条件的数据权限
            if(this.compositeCondition(item.dataRules).length > 0) { 
              dataRules.push({
                'roleId': roleId,
                'routerId': routerId,
                'metaId': item.id,
                'conditions': JSON.stringify(item.dataRules)
              })
            }
          })

          this.$emit('input', dataRules);
          this.visible = false          
        }
      })
    },
    onDialogClose() {
      this.visible = false
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openDataPermission', function(code, roleId, routerId) {
        this.roleId = roleId
        this.routerId = routerId
        let metas = require('@/views/' + code + '/metadata').metadata
        // 合并角色配置的数据权限, 并去除metadata.js已经删除的元数据
        this.metaFormModel.metaDatas = []
        metas.forEach((item, idx) => {
          let noFound = true
          this.value.forEach((rule) => {
            if(item.id == rule.metaId && rule.routerId == routerId) {
              this.metaFormModel.metaDatas.push({
                id: item.id,
                name: item.name,
                schemeId: item.schemeId,
                dataRules: JSON.parse(rule.conditions)
              })
              noFound = false
            }
          })
          // 配置后，新添加的元数据
          if(noFound) {
            this.metaFormModel.metaDatas.push({
              id: item.id,
              name: item.name,
              schemeId: item.schemeId,
              dataRules: {
                groupOne: [
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []},
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []},
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []}      
                ],
                groupTwo: [
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []},
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []},
                  {logic: 'AND', column: null, queryType: '', value: null, operations: []}
                ],
                groupLogic: 'OR'
              }
            })
          }

          this.$nextTick(() => {
            this.$refs.conditionPanel[idx].$emit('init')
          })
        })
        this.visible = true
      })
    }) 
  }  
}
</script>
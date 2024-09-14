<template>
  <el-dialog :title='dialogProps.title' :visible.sync='dialogProps.visible' width='640px' :close-on-click-modal='false' :lock-scroll='true' v-loading='loading'>
    <div slot='title' class='dialog-header'>{{ dialogProps.title }}</div>
    <data-permission ref='dataPermission' v-model='dataPermissions'></data-permission>
    <el-row :gutter='15'>
      <el-col :span='24'>
        <div class='permission-container'>
          <el-input
            style="margin-bottom: 10px"
            placeholder="输入关键字进行搜索"
            v-model="searchText">
          </el-input>
          <div class="tree-container">
            <el-tree ref='permissionTree'
                     :data='permissionTreeNodes'
                     :filter-node-method="searchNode"
                     show-checkbox
                     :props='{children: "children", label: "name"}'
                     :highlight-current='true'
                     :default-expand-all='true'
                     node-key='key'
                     :default-checked-keys='permissionKeys'
                     :expand-on-click-node='false'>
            <span class='custom-tree-node' slot-scope='{ node, data }'>
              <span>{{ node.label }}</span>
              <span v-if='canDataPermission(data, node)'>
                <el-button type='text' @click='() => onDataPermission(data)'>数据权限</el-button>
              </span>
            </span>
            </el-tree>
          </div>
        </div>
      </el-col>
    </el-row>
    <span slot='footer' class='dialog-footer'>
      <el-button type='primary' :plain='true' @click='doSavePermission()'>确 定</el-button>
      <el-button :plain='true' @click='handleDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treePermission, listResourcePermissionByRoleId, savePermission, listDataPermissionByRoleId } from '@/api/admin/common/permission'
import DataPermission from './dataPermistion'
import BaseUI from '@/views/components/baseUI'
export default {
  extends: BaseUI,
  name: 'role-permission',
  components: {
    DataPermission
  },
  data() {
    return {
      loading: false,
      searchText: '',
      permissionTreeNodes: [],  // 可用的路由和资源树结构
      permissionKeys: [],     // 已分配权限资源Id列表
      dataPermissions: [],    // 数据权限列表
      role: {
        id: 0,
        name: ''
      },
      dialogProps: {
        visible: false,
        title: ''
      }
    }
  },
  watch: {
    searchText(val) {
      this.$refs.permissionTree.filter(val);
    }
  },
  methods: {
    searchNode(value, data, node) {
      if (!value) return true
      let parent = node.parent
      let labels = [node.label]
      let level = 1
      while (level < node.level) {
        labels = [...labels, parent.label]
        parent = parent.parent
        level++
      }
      return labels.some(label => label.indexOf(value) !== -1)
    },
    handleDialogClose() {
      this.dialogProps.visible = false
    },
    async componentInit(role) {
      this.loading = true
      this.role = role
      this.dialogProps.title = '权限设置 - ' + this.role.name
      this.permissionKeys = []
      this.dataPermissions = []
      try {
        let [listPermissionRespData, treePermissionRespData, listDataPermissionRespData] = await Promise.all([
          listResourcePermissionByRoleId(role.id),
          treePermission(),
          listDataPermissionByRoleId(role.id)
        ])
        if(listPermissionRespData.code == 100 && treePermissionRespData.code == 100 && listDataPermissionRespData.code == 100) {
          this.permissionTreeNodes = treePermissionRespData.data
          this.permissionKeys = listPermissionRespData.data
          this.dataPermissions = listDataPermissionRespData.data

        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : (treePermissionRespData.code != 100 ? treePermissionRespData : listDataPermissionRespData))
        }

        this.loading = false
      } catch(error) {
        this.outputError(error)
      }
    },
    doSavePermission() {
      this.loading = true
      let routerIds = []
      let resourceIds = []
      this.$refs.permissionTree.getCheckedNodes().forEach(element => {
        if(element.type === 'router') {
          routerIds.push(element.id)
        }
        if(element.type === 'resource') {
          resourceIds.push(element.id)
        }
      })
      this.$refs.permissionTree.getHalfCheckedNodes().forEach(element => {
        if(element.type === 'router') {
          routerIds.push(element.id)
        }
        if(element.type === 'resource') {
          resourceIds.push(element.id)
        }
      })

      let permission = {
        routerIds: routerIds,
        resourceIds: resourceIds,
        dataPermissions: this.dataPermissions.filter(item => {
            return routerIds.indexOf(item.routerId) >= 0
          })
      }
      savePermission(this.role.id, permission).then(responseData => {
        if(responseData.code == 100) {
          this.dialogProps.visible = false
        } else {
          this.showMessage(responseData)
        }
        this.loading = false
      }).catch(error => {
        this.outputError(error)
      })
    },
    isRouterLeaf(data) {
      if(!(data.children instanceof Array) || data.children.length <= 0) {
        return true
      }
      for(let idx = 0; idx < data.children.length; idx++) {
        let item = data.children[idx]
        if(item.type == 'router') {
          return false
        }
      }
      return true
    },
    canDataPermission(data, node) {
      if(data.type == 'router' && this.isRouterLeaf(data)) {
        // 全选 或半选状态
        if(this.isHalfOrChecked(node)) {
          // 元数据配置文件存在
          try {
            let metas = require('@/views/' + data.code + '/metadata').metadata
            return true
          }catch (e) {
            console.warn('没有找到元数据描述文件：%s', '@/views/' + data.code + '/metadata')
          }
        }
      }
      return false
    },
    isHalfOrChecked(node) {
      if(node.checked) {
        return true
      }
      if(!(node.childNodes instanceof Array) || node.childNodes.length <= 0) {
        return false
      }

      for(let childNod of node.childNodes) {
        if(this.isHalfOrChecked(childNod)) {
          return true
        }
      }

      return false
    },
    onDataPermission(data) {
      if(data.code) {
        this.$refs.dataPermission.$emit('openDataPermission', data.code, this.role.id, data.id)
      }
    }
  },
  mounted: function() {
    this.$nextTick(() => {
      this.$on('openSetPermissionDialog', function(role) {
        this.dialogProps.visible = true
        this.searchText = ''
        this.componentInit(role);
      })
    })
  }
}
</script>
<style lang='scss' scoped>
  .permission-container {
    .tree-container {
      height: 360px;
      overflow-y: auto;
    }
  }
  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
  }
</style>



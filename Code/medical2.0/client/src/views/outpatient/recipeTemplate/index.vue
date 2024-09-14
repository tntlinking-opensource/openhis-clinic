<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow'></History>
    <!-- 编辑窗口  -->
    <recipetemplate-form ref='recipetemplateForm' :permission='permission'
                         v-on:save-finished='getRecipetemplateList()'></recipetemplate-form>
    <div class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-row v-if='!moreCodition' class='search-row'>
          <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm'
                   :inline-message='true'>
            <el-col :span="4">
              <el-form-item label='模板名称' prop='recipetemplateName'>
                <!-- <div style="margin-left:40px;">  -->
                <el-input v-model='queryModel.recipetemplateName' :clearable='true' placeholder='请输入模板名称'></el-input>
                <!-- </div> -->
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='模板编号' prop='doce'>
                <!-- <div style="margin-left:40px;">  -->
                <el-input v-model='queryModel.code' :clearable='true' placeholder='请输入模板编号'></el-input>
                <!-- </div> -->
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='处方类型' prop='type'>
                <el-select v-model="queryModel.type" placeholder="请选择处方类型">
                  <el-option
                    v-for="type in type_List"
                    :key="type.value"
                    :label="type.name"
                    :value="type">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label='模板类别' prop='category'>
                <el-radio v-model="queryModel.category" label="0">私人模板</el-radio>
                <el-radio v-model="queryModel.category" label="1">公开模板</el-radio>
                <el-radio v-model="queryModel.category" label="2">AI模板</el-radio>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label='日期' prop='updateDate'>
                <el-date-picker
                  v-model="queryModel.updateDate"
                  type="datetimerange"
                  align="right"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  format="yyyy-MM-dd HH:mm:ss"
                  :default-time="['00:00:00', '23:59:59']">
                </el-date-picker>
              </el-form-item>
            </el-col>

            <el-col :span="3" style="text-align:right;padding-right:5px;float:right;">
              <el-button-group>
                <el-button
                  type="primary"
                  icon="el-icon-plus"
                  @click="onCreateTemplate()"
                >添加
                </el-button
                >
              </el-button-group>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around;float:right;">
              <el-button
                type="primary"
                icon="el-icon-search"
                @click="onSearch()"
                :plain="true"
              >搜索
              </el-button>
              <el-button
                type="info"
                icon="el-icon-refresh-left"
                @click="reset"
                :plain="true"
              >重置
              </el-button>
            </el-col>

          </el-form>
        </el-row>
        <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId'
                   @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->
      <!-- <div class="page-container-header-end">
        <div>
          <el-button v-show='permission.add' type='primary' icon='el-icon-plus' :plain='true' @click='onCreateRecipetemplate()'>添加</el-button>
        </div>
      </div> -->
      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table class='drag_table' ref="tableRef" height="calc(100vh - 240px)" :data='recipetemplateList' border
                      @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName'
                      :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
                label="序号"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop'
                               :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align'
                               :min-width='cv.miniWidth+"px"' :width='cv.width+"px"' header-align='center'
                               :column-key='index.toString()' :render-header="renderHeader">
                <template slot-scope='{row,$index}'>
                  <span
                    v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                    <!-- <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check' style='color:#F56C6C;'>

                    </li> -->
                    <!-- <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' >

                   </li> -->
                    <span v-if='getAttrValue(row, columnViews[index].prop) == "1"'>
                      公开模板
                    </span>
                     <span v-else>
                      私人模板
                    </span>
                  </span>
                  <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType) }}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' :key="Math.random()" fixed="right">
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn'
                                       v-on:show-all-column='showAllColumn'
                                       v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='recipetemplateList' :tHeader='getHeads()'
                                       :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start'
                                 icon-name='el-icon-view'
                                 @click='onViewRecipetemplate(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start'
                                 icon-name='el-icon-edit'
                                 @click='onEditRecipetemplate(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start'
                                 icon-name='el-icon-document'
                                 @click='onCopyRecipetemplate(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.remove' type='danger' content='删除' placement='top-start'
                                 icon-name='el-icon-delete'
                                 @click='onDeleteRecipetemplate(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start'
                                 icon-name='el-icon-info'
                                 @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination
            background
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page.sync='currentPage'
            :page-sizes='[20, 50, 100, recipetemplateTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='recipetemplateTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </div>

    <!-- 添加弹出框 -->
    <el-dialog
      title="添加处方模板"
      :visible.sync="templateDialogVisible"
      width="30%"
      :before-close="handleClose">
      <er-row v-for="(item,index) in type_List" :key="index">
        <!-- <div v-for="(item,index) in type_List" :key="index"> -->
        <el-col :span="12">
          <div style="margin-left:60px;margin-top:20px;">
            <el-button class="buttonClass" type="info" plain @click="add(item,index)">{{ item.name }}</el-button>
          </div>
        </el-col>

        <!-- </div> -->
      </er-row>
      <!-- <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span> -->
    </el-dialog>


  </el-row>
</template>

<script>
  import {validatenull} from '@/utils/validate'
  import {listRecipetemplatePage, getRecipetemplateById, deleteRecipetemplate} from '@/api/outpatient/recipetemplate'
  import {listResourcePermission} from '@/api/admin/common/permission'
  import RecipetemplateForm from './recipetemplateForm'
  import ExportExcelButton from '@/components/ExportExcelButton'
  import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
  import QueryForm from '@/views/components/queryForm'
  import MainUI from '@/views/components/mainUI'
  import OperationIcon from '@/components/OperationIcon'
  import History from '@/views/components/history'
  import {listDictItemAll} from '@/api/sys/dictItem'

  export default {
    extends: MainUI,
    components: {
      RecipetemplateForm,
      ExportExcelButton,
      ViewColumnsSelect,
      QueryForm,
      OperationIcon,
      History
    },
    data() {
      return {
        permission: {
          view: false,
          add: false,
          edit: false,
          remove: false,
          export: false
        },
        queryTypes: {},
        queryModel: {
          recipetemplateName: "",
          code: "",
          type: {},
          category: "",
          updateDate: "",
        },
        search: {
          params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],
          offset: 0,
          limit: 20,
          columnName: '',      // 排序字段名
          order: ''            // 排序
        },
        currentPage: 1,
        recipetemplateTotal: 0,
        recipetemplateList: [],


        oprColumnWidth: 140,  // 操作列宽
        tableId: '1186832625065336477',
        schemeId: '1186832625065336461',
        templateDialogVisible: false,
        type_List: [],
      }
    },
    methods: {
      indexMethod(index) {
        return (this.currentPage - 1) * this.search.limit + index + 1;
      },
      reset() {
        this.queryModel = {
          recipetemplateName: "",
          type: {},
          category: "",
          updateDate: "",

        }
        this.getRecipetemplateList()
      },
      add(item, index) {
        // if(index==0){
        this.$refs.recipetemplateForm.$emit('openAddRecipetemplateDialog', item, index, currentUser.company)
        this.templateDialogVisible = false
        //}
      },
      onCreateTemplate() {
        // this.$refs.recipetemplateForm.$emit('openAddRecipetemplateDialog')
        let type_search = {
          params: [{'columnName': 'dict_type_id', 'queryType': '=', 'value': '1014474470772899974'}]
        }
        // 字段对应表上filter条件
        type_search.params.push.apply(type_search.params, [])
        // 数据权限: 字典项sys_dict_item
        this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
        this.type_List.splice(0, this.type_List.length)
        listDictItemAll(type_search).then(responseData => {
          this.type_List = responseData.data
          this.type_List = this.type_List.filter((item) => item.name != "零售处方")
          this.type_List = this.type_List.filter((item) => item.name != '附加费')
          this.templateDialogVisible = true
        })
      },

      getRecipetemplateList() {
        this.setLoad()
        /* 查询参数 和数据权限 */
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        if (this.moreCodition) {
          this.search.params = this.search.params.concat(this.compositeCondition())
        } else {

          this.search.params.push({
            columnName: 'recipetemplate_name',
            queryType: 'like',
            value: this.queryModel.recipetemplateName
          })

          this.search.params.push({
            columnName: 'code',
            queryType: 'like',
            value: "%" + this.queryModel.code ? this.queryModel.code : "" + "%"
          })
          this.search.params.push({
            columnName: 'type',
            queryType: '=',
            value: validatenull(this.queryModel.type.value) ? '' : this.queryModel.type.value
          })
          if (this.queryModel.category != "" && this.queryModel.category == 0) {
            this.search.params.push({
              columnName: "category",

              queryType: '=',
              value: "0",
            })
            this.search.params.push(
              {
                columnName: "create_id",
                logic: 'and',
                queryType: '=',
                value: currentUser.id,
              }
            )
          } else if (this.queryModel.category != "" && this.queryModel.category == 1) {
            this.search.params.push({
              columnName: "category",
              queryType: '=',
              value: "1",

            })
          }
          //   this.search.params.push({
          //   columnName: 'update_date',
          //   queryType: 'like',
          //   value: this.queryModel.updateDate
          //  })
          if (this.queryModel.updateDate && this.queryModel.updateDate.length) {
            this.search.params.push({
              logic: "AND",
              queryType: "("
            }, {
              columnName: "update_date",
              logic: "",
              queryType: 'between',
              value: this.queryModel.updateDate,
            }, {
              logic: "",
              queryType: ")"
            })
          }

        }
        // 数据权限: 模板处方表recipetemplate
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        listRecipetemplatePage(this.search).then(responseData => {
          if (responseData.code == 100) {
            this.recipetemplateTotal = responseData.data.total
            this.recipetemplateList = responseData.data.rows
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onSearch() {
        if (this.moreCodition) {
          this.search.offset = 0
          this.currentPage = 1
          this.getRecipetemplateList()
        } else {
          this.$refs['queryForm'].validate(valid => {
            if (valid) {
              this.search.offset = 0
              this.currentPage = 1
              this.getRecipetemplateList()
            } else {
              return false
            }
          })
        }
      },
      onSizeChange(val) {
        this.currentPage = 1
        this.search.limit = val;
        this.search.offset = (this.currentPage - 1) * val
        this.getRecipetemplateList()
      },
      onCurrentChange(val) {
        this.search.offset = (val - 1) * this.search.limit
        this.currentPage = val
        this.getRecipetemplateList()
      },
      async pageInit() {
        this.setLoad()
        try {
          this.initOptions(this.queryModel)
          this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
          this.search.params.push({
              logic: "AND",
              queryType: "("
            }, {
              columnName: "category",
              logic: "",
              queryType: '=',
              value: 0,
            },
            {
              columnName: "create_id",
              logic: "and",
              queryType: '=',
              value: currentUser.id,
            },
            {
              columnName: "category",
              logic: "or",
              queryType: '=',
              value: 1,
            },
            {
              logic: "",
              queryType: ")"
            })
          // 数据权限: 模板处方表recipetemplate
          this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
          let [listRecipetemplateRespData, listPermissionRespData] = await Promise.all([
            listRecipetemplatePage(this.search),
            listResourcePermission(this.$route.meta.routerId)
          ])
          if (listRecipetemplateRespData.code == 100 && listPermissionRespData.code == 100) {
            this.recipetemplateTotal = listRecipetemplateRespData.data.total
            this.recipetemplateList = listRecipetemplateRespData.data.rows
            console.log(listPermissionRespData.data, '这是个什么');
            this.permission.view = listPermissionRespData.data.find(item => {
              return item.permission === 'recipeTemplate:read'
            })
            this.permission.export = listPermissionRespData.data.find(item => {
              return item.permission === 'recipeTemplate:export'
            })
            this.permission.add = listPermissionRespData.data.find(item => {
              return item.permission === 'recipeTemplate:create'
            })
            this.permission.edit = listPermissionRespData.data.find(item => {
              return item.permission === 'recipeTemplate:update'
            })
            this.permission.remove = listPermissionRespData.data.find(item => {
              return item.permission === 'recipeTemplate:delete'
            })
          } else {
            this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listRecipetemplateRespData)
          }
          this.resetLoad()
        } catch (error) {
          this.outputError(error)
        }
        let type_search = {
          params: [{'columnName': 'dict_type_id', 'queryType': '=', 'value': '1014474470772899974'}]
        }
        //   // 字段对应表上filter条件
        //   type_search.params.push.apply(type_search.params, [])
        // // 数据权限: 字典项sys_dict_item
        // this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
        // this.type_List.splice(0, this.type_List.length)
        listDictItemAll(type_search).then(responseData => {
          this.type_List = responseData.data
          this.type_List = this.type_List.filter((item) => item.name != "零售处方")
          this.type_List = this.type_List.filter((item) => item.name != '附加费')
        })
      },
      onViewRecipetemplate(index, row) {
        this.setLoad()
        getRecipetemplateById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.recipetemplateForm.$emit('openViewRecipetemplateDialog', responseData.data, currentUser.company)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onCreateRecipetemplate() {
        this.$refs.recipetemplateForm.$emit('openAddRecipetemplateDialog', currentUser.company)
      },
      onEditRecipetemplate(index, row) {
        this.setLoad()
        getRecipetemplateById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.recipetemplateForm.$emit('openEditRecipetemplateDialog', responseData.data, currentUser.company)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onCopyRecipetemplate(index, row) {
        this.setLoad()
        getRecipetemplateById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.recipetemplateForm.$emit('openCopyRecipetemplateDialog', responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onDeleteRecipetemplate(index, row) {
        this.$confirm('确定删除吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.setLoad()
          deleteRecipetemplate(row).then(responseData => {
            if (responseData.code == 100) {
              this.getRecipetemplateList()
              this.showMessage({type: 'success', msg: '删除成功'})
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          }).catch(error => {
            this.outputError(error)
          })
        }).catch(() => {
        })
      },
      onSortChange(orderby) {
        if (validatenull(orderby.prop)) {
          this.search.columnName = ''
          this.search.order = ''
        } else {
          this.search.columnName = orderby.prop
          this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
        }

        this.getRecipetemplateList()
      },
      initOptions(This) {
        let company = JSON.parse(sessionStorage.getItem("currentUser")).company;

      }
    },
    watch: {
      // tableData是el-table绑定的数据
      tableData: {
        // 解决表格显示错位问题
        handler() {
          this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
          })
        },
        deep: true
      }
    },
    updated() {
      if (this.$refs.tableRef) {
        this.$nextTick(() => {
          // tableRef是el-table绑定的ref属性值
          this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
        })
      }
    },
    mounted() {
      this.pageInit()
      this.initOptions();

    }
  }
</script>
<style scoped lang="scss">

  .buttonClass {
    width: 120px;
    height: 40px;
    padding: 0;
    //控制图标icon大小
    /deep/ i {
      font-size: 20px;
    }

    //控制字大小
    /deep/ {
      font-size: 20px;
    }
  }


</style>
<style lang="scss" scoped>
  .drag_table {
    // 设置表格header的高度
    /deep/ th {
      height: 44px;
    }

    /deep/ th.gutter:last-of-type {
      height: 0 !important;
    }

    // 设置表格body的高度
    /deep/ .el-table__body-wrapper {
      //解决数据展示超出body高度不滚动bug
      overflow-y: auto;
      // 减去的是表格header的高度
      height: calc(100vh - 44px) !important;
    }
  }
</style>
<style scoped>
  /deep/ .el-table__body-wrapper {
    height: calc(100% - 44px) !important;
  }
</style>

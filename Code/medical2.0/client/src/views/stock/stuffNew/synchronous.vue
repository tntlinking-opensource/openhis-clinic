<template>
  <el-dialog :title="dialogProps.title" :visible.sync="dialogProps.visible" :close-on-click-modal="false" width="90%"
    v-loading="loading" :destroy-on-close="true">
    <el-row>
      <!-- 编辑窗口  -->
      <synchronousStuffForm ref="synchronousStuffFormRef" :permission='permission' v-on:save-finished='getStuffList'>
      </synchronousStuffForm>

      <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm'
              :inline-message='true'>
              <el-col :span="5">
                <el-form-item label='材料名称' prop='name'>
                  <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入材料名称'></el-input>
                </el-form-item>
              </el-col>

              <el-col :span="3" style="display:flex;justivy-content:space-around">
                <el-button type="primary" icon="el-icon-search" @click="onSearch()" :plain="true">搜索
                </el-button>
                <el-button type="info" icon="el-icon-refresh-left" @click="reset" :plain="true">重置
                </el-button>
              </el-col>
              <el-col :span="16" style="text-align:right;padding-right:5px">
                <el-button-group>
                  <div style="display: flex;align-items: center;">
                    <div style="margin-right: 10px;">已选择{{ currentData.length }}条</div>
                    <div style="display: flex;align-items: center;">
                      <div style="width: 80px;">数据来源</div>
                      <el-select @change="handleSource" v-model="source">
                        <el-option label="租户" value="1"></el-option>
                        <el-option label="院版His" value="2"></el-option>
                      </el-select>
                    </div>
                  </div>
                  <!-- <el-button v-show="permission.add" type="primary" icon="el-icon-plus" @click="onCreateStuff()">添加
                  </el-button> -->
                  <!-- <el-dropdown v-show="permission.add" style="padding-left:10px">
                    <span class="el-dropdown-link">
                      批量操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item>
                        <el-button type="text" @click="indateClick">批量设置有效期预警</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item>
                        <el-button type="text" @click="inventoryClick">批量设置库存预警</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item>
                        <el-button type="text" @click="importStudentExcel">批量导入</el-button>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown> -->
                </el-button-group>
              </el-col>
            </el-form>
          </el-row>
          <!-- <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId'
            @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm> -->
        </div>
        <el-row>
          <el-col :span='24'>
            <div>
              <el-table ref="tableRef" height="calc(100vh - 280px)" class='drag_table' :data='stuffList' border
                @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName'
                :header-cell-class-name='headerCellClassName' highlight-current-row :row-key="getRowKey"
                @selection-change="handleSelectionChange">
                <!-- 多选框 -->
                <el-table-column type='selection' :reserve-selection="true" width='50' align='center'
                  :selectable="selectable">
                </el-table-column>
                <el-table-column label="序号" type="index" :index="indexMethod" align="center">
                </el-table-column>
                <!-- <el-table-column v-for="(cv, index) in columnViews" v-if='cv.display' :prop='cv.prop'
                  :key="`columnViews_${index}`" :label='cv.label' sortable='custom' :align='cv.align'
                  :min-width='cv.miniWidth + "px"' :width='cv.width + "px"' header-align='center'
                  :column-key='index.toString()' :render-header="renderHeader">
                  <template slot-scope='{row,$index}'>
                    <span
                      v-if='columnViews[index].showType == "Switch" || columnViews[index].showType == "Checkbox" || columnViews[index].showType == "Radio"'>
                      <li v-if='getAttrValue(row, columnViews[index].prop) == "1"' class='el-icon-check'
                        style='color:#F56C6C;'></li>
                    </span>

                    <span v-else>{{ getAttrValue(row, columnViews[index].prop, columnViews[index].javaType) }}</span>
                  </template>
</el-table-column> -->
                <el-table-column v-for="item in columnList" :label="item.label" :prop="item.prop"></el-table-column>
                <el-table-column prop="inventory" label="可用库存" sortable="custom" width="100px" v-if="source == 1">
                  <template slot-scope="scope">
                    <span v-if="scope.row.stock.surplusStock != null">
                      {{
    Math.floor(scope.row.stock.surplusStock / scope.row.packNumber) >= 0 ?
      Math.floor(scope.row.stock.surplusStock / scope.row.packNumber) + scope.row.packUnit.name +
      ((scope.row.stock.surplusStock % scope.row.packNumber > 0) ? (scope.row.stock.surplusStock %
        scope.row.packNumber) + scope.row.minUnit.name : "") : scope.row.stock.surplusStock +
      scope.row.packUnit.name
  }}
                    </span>
                    <span v-else>0</span>
                  </template>
                </el-table-column>
                <!-- <el-table-column label='操作' header-align='center' :width='50 + "px"' :key="Math.random()" fixed="right">
                  <template slot='header' slot-scope="scope">
                    <span>操作</span>
                  </template>
                  <template slot-scope='scope'>
                    <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start'
                      icon-name='el-icon-view' @click='onViewStuff(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start'
                      icon-name='el-icon-edit' @click='onEditStuff(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start'
                      icon-name='el-icon-document' @click='onCopyStuff(scope.$index, scope.row)'></OperationIcon>
                    <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start'
                      icon-name='el-icon-info' @click='onShowHistory(scope.$index, scope.row)'></OperationIcon>
                  </template>
                </el-table-column> -->
              </el-table>
            </div>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span='24'>
            <el-pagination background @size-change='onSizeChange' @current-change='onCurrentChange'
              :current-page.sync='currentPage' :page-sizes='[20, 50, 100, stuffTotal]' :page-size='20'
              layout='total, sizes, prev, pager, next, jumper' :total='stuffTotal'>
            </el-pagination>
          </el-col>
        </el-row>
      </el-card>
      <!-- 批量设置有效期预警 -->
      <div class="stuff_indate">
        <el-dialog title="批量设置有效期" :visible.sync="indateDialogVisible" width="30%" :before-close="handleClose">
          <!-- <el-divider style="margin-top:-30px;"></el-divider> -->
          <div style="padding-bottom:10px;margin-top:-20px">
            <el-alert title="批量设置后将修改所有药品的效期预警时间" :closable="false" type="warning">
            </el-alert>
          </div>
          <span>药品有效期的预警时间为</span>
          <el-input v-model="indate" oninput="value=value.replace(/[^\d.]/g,'')">
            <template slot="append">
              天
            </template>
          </el-input>
          <span slot="footer" class="dialog-footer">
            <el-button @click="indateSave(0)">取 消</el-button>
            <el-button type="primary" @click="indateSave(1)">确 定</el-button>
          </span>
        </el-dialog>

        <!-- 库存预警 -->
        <el-dialog title="批量设置库存预警" :visible.sync="inventoryDialogVisible" width="30%"
          :before-close="handleCloseInventory">
          <!-- <el-divider style="margin-top:-30px;"></el-divider> -->
          <div style="padding-bottom:10px;margin-top:-20px">
            <el-alert title="批量设置后将修改所有药品的库存预警数量（按包装代为预警）" :closable="false" type="warning">
            </el-alert>
          </div>
          <span>药品的库存预警数量为</span>
          <el-input v-model="inventoryFloor" oninput="value=value.replace(/[^\d.]/g,'')">
          </el-input>
          <span slot="footer" class="dialog-footer">
            <el-button @click="inventorySave(0)">取 消</el-button>
            <el-button type="primary" @click="inventorySave(1)">确 定</el-button>
          </span>
        </el-dialog>

        <!-- 点击导入后弹出框 -->
        <el-dialog title="导入材料" :visible.sync="importDialogVisible" width="45%" :before-close="cancellation">
          <input ref="file" type="file" accept=".xlsx,.xls" style="display: none;" @change="uploadFile">
          <el-button type="primary" icon="el-icon-upload" @click="clickFile">选择文件</el-button>
          <el-button type="primary" icon="el-icon-download" @click="downloadExcel">模板下载</el-button>
          <el-button @click="cancellation()">取 消</el-button>
        </el-dialog>

        <el-dialog title="材料信息导入错误" :visible.sync="dialogVisible" width="30%" :before-close="mistakeClose">
          <span> 导入成功<span style="white-space:pre;color:#08ff00;font-size: 16px">{{ chengGong
              }}</span>条&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
          </span>
          <span> 导入失败<span style="white-space:pre;color:red;font-size: 16px">{{ shiBai }}</span>条 </span><br>
          <span style="white-space:pre;color:red;font-size: 15px">{{ mistake }}</span>
          <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
          </span>
        </el-dialog>
      </div>
    </el-row>
    <span slot='footer' class='dialog-footer'>
      <el-button :disabled="flage" type='primary' :plain='true' @click='onSubmit()'>同 步</el-button>
      <el-button v-if='dialogProps.action != "view"' :plain='true' @click='onDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { validatenull } from '@/utils/validate'
import { listParentStuffPage, listInstitutionStuffPage, syncStuff, syncInstitutionStuff, getStuffById, deleteStuff, updateAllIndate, updateAllInventory, inventory, uploadExcel } from '@/api/stock/stuff'
import { listResourcePermission } from '@/api/admin/common/permission'
import synchronousStuffForm from './synchronousStuffForm'
import { listDictItemAll } from '@/api/sys/dictItem'
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import { getLocalCurrentCompany } from "@/utils/auth";
import fileurl from "@/assets/file/stuff.xlsx";
import BaseUI from "@/views/components/baseUI";


export default {
  extends: MainUI,
  name: "stuff-form",
  components: {
    synchronousStuffForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
  },
  data() {
    return {
      dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      inventory: null,
      mistake: null,
      chengGong: null,
      shiBai: null,
      dialogVisible: false,
      systemParamConfigSearch: {
        params: []
      },
      permission: {
        view: false,
        add: false,
        edit: false,
        remove: false,
        export: false
      },
      queryTypes: {
        'name': 'like',
        'type': '=',
        'bar_code': 'like',
      },
      queryModel: {
        'name': '',   // 材料名称
        'type': {     // 材料类型
          'value': '',
          'name': '',
        },
        'barCode': '',   // 条形码
        'status': '',
      },
      status: [
        {
          value: '0',
          label: '否'
        },
        {
          value: '1',
          label: '是'
        },
      ],//是否启用
      isOutSell: [
        {
          value: '0',
          label: '否'
        },
        {
          value: '1',
          label: '是'
        },
      ],
      search: {
        params: [{ columnName: 'company_id', queryType: '=', value: currentUser.company.id }],
        offset: 0,
        limit: 20,
        columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      stuffTotal: 0,
      stuffList: [],

      typeList: [],    // 材料类型

      oprColumnWidth: 140,  // 操作列宽
      tableId: '1004462867645374480',
      schemeId: '1004462867645374509',
      indateDialogVisible: false,  //批量设置有效期弹出框
      inventoryDialogVisible: false,   //批量设置库存预警弹出框
      importDialogVisible: false, // 批量导入弹框变量
      indate: "",   //有效期
      inventoryFloor: "",   //库存下限
      currentData: [],
      source: '1',
      columnList: [],
      columnListOne: [
        {
          prop: 'name',
          label: '材料名称',
        },
        {
          prop: 'commonName',
          label: '常用名',
        },
        {
          prop: 'code',
          label: '材料编码',
        },
        {
          prop: 'typeName',
          label: '材料类型',
        },
        {
          prop: 'factoryName',
          label: '生产厂家',
        },
        {
          prop: 'natureName',
          label: '性质',
        },
        {
          prop: 'barCode',
          label: '条形码',
        },
        {
          prop: 'syncNumName',
          label: '是否已同步',
        },
        {
          prop: 'createDate',
          label: '创建日期',
        },
        {
          prop: 'updateDate',
          label: '更新日期',
        },
      ],
      // 院版材料列表
      columnListTwo: [
        {
          prop: 'sfxmmc',
          label: '材料名称',
        },
        {
          prop: 'py',
          label: '拼音码',
        },
        {
          prop: 'sfxmCode',
          label: '材料编码',
        },
        {
          prop: 'dwjls',
          label: '单位计量数',
        },
        {
          prop: 'dw',
          label: '最小单位',
        },
        {
          prop: 'gg',
          label: '包装规格',
        },
        {
          prop: 'dj',
          label: '对外销售价格',
        },
        {
          prop: 'syncNumName',
          label: '是否已同步',
        },
      ]

    };
  },
  props: {
    // 权限
    // permission: {
    //   type: Object,
    // },

  },
  methods: {
    getRowKey(row) {
      return row.sfxmId || row.id;
    },
    handleSource(val) {
      this.source = val
      if (this.source == 1) {
        this.columnList = this.columnListOne
      } else {
        this.columnList = this.columnListTwo
      }
      this.search.offset = 0
      this.currentPage = 1
      this.$refs.queryForm.resetFields()

      this.pageInit()
    },
    selectable(row, index) {
      if (row.syncNum == 1) {
        return false
      } else {
        return true
      }
    },
    handleSelectionChange(val) {
      this.currentData = val;
    },
    async onSubmit() {
      if (this.currentData.length == 0) {
        this.$message({
          message: '请选择要同步的数据',
          type: 'warning'
        });
        return;
      }
      const functionSync = this.source == '1' ? syncStuff : syncInstitutionStuff
      const res = await functionSync(this.currentData);
      if (res.code == 100) {
        this.$message({
          message: '同步成功',
          type: 'success'
        });
        this.dialogProps.visible = false
        this.$emit('update')
      } else {
        this.$message({
          message: res.msg,
          type: 'error'
        });
      }
    },
    onDialogClose() {
      this.dialogProps.visible = false
    },
    handleClose() {
      this.indateSave(0)
    },
    handleCloseInventory() {
      this.inventorySave(0)
    },
    //批量设置有效期保存
    indateSave(index) {
      if (index == 0) {
        this.indateDialogVisible = false
        this.indate = ""
      } else {
        console.log(this.indate);
        let stuff = {
          indate: this.indate,
          company: currentUser.company
        }
        updateAllIndate(stuff).then((res) => {
          if (res.code == 100) {
            this.indateDialogVisible = false
            this.indate = ""
            this.getStuffList();
            this.$message.success("修改成功")
          }
        }).catch((error) => {
          this.$message.error(error)
        })
      }
    },

    //批量设置库存预警保存
    inventorySave(index) {
      if (index == 0) {
        this.inventoryDialogVisible = false
        this.inventoryFloor = ""
      } else {
        let stuff = {
          inventoryFloor: this.inventoryFloor,
          company: currentUser.company
        }
        updateAllInventory(stuff).then((res) => {
          if (res.code == 100) {
            this.inventoryDialogVisible = false
            this.inventoryFloor = ""
            this.getStuffList();
            this.$message.success("修改成功")
          }
        }).catch((error) => {
          this.$message.error(error)
        })
      }
    },

    // 下载模板
    downloadExcel() {
      let a = document.createElement('a');
      let evt = document.createEvent('MouseEvents');
      a.download = '材料信息';
      a.href = fileurl;
      evt.initEvent('click', true, true);
      a.dispatchEvent(evt);
      window.URL.revokeObjectURL(a.href);
    },


    // 关闭导入报错
    mistakeClose(done) {
      this.$confirm('确认关闭？')
        .then(_ => {
          done();
        })
        .catch(_ => { });
    },

    // 批量导入按钮点击事件
    importStudentExcel() {
      this.importDialogVisible = true;
      console.log(this.importDialogVisible)
    },

    // 选择文件事件
    clickFile() {
      this.$refs.file.dispatchEvent(new MouseEvent('click'))
    },

    // 上传文件
    async uploadFile() {
      const file = this.$refs.file.files
      var extName = file[0].name.substring(file[0].name.lastIndexOf('.')).toLowerCase()
      if (extName === '.xlsx' || extName === '.xls') {
        var formData = new FormData()
        let id = currentUser.company.id;
        console.log("看看这里呀呀呀" + id)
        formData.append('file', file[0])
        this.$message.success('正在导入中，请耐心等待')
        uploadExcel(formData).then((res) => {
          console.log("到这里了吗extName === " + formData)
          if (res.code === '100') {
            if (res.data[2] === "") {
              this.$message({
                type: 'success',
                message: '数据导入成功,共导入' + res.data[0] + '条信息!'
              })
              this.cancellation();
            } else {
              console.log("导入报错" + this.mistake)
              this.chengGong = res.data[0]
              this.shiBai = res.data[1]
              this.mistake = res.data[2]
              this.dialogVisible = true
              this.cancellation();
            }
          } else {
            this.$message({
              type: "error",
              message: res.resultJson.msg
            })
            this.cancellation();
          }
        })
      }
      else {
        this.$message.error('数据导入失败，请选择正确的xlsx模板文件')
        this.cancellation();
      }
      this.load();
    },

    // 关闭导入窗口
    cancellation() {
      this.importDialogVisible = false;
      this.reset();
    },



    //批量设置库存弹出框
    inventoryClick() {
      this.inventoryDialogVisible = true
    },

    //批量设置有效期弹出框
    indateClick() {
      this.indateDialogVisible = true
    },


    indexMethod(index) {
      return (this.currentPage - 1) * this.search.limit + index + 1;
    },
    reset() {
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getStuffList(val) {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = [{ columnName: 'company_id', queryType: '=', value: currentUser.company.id }]
      if (val == "1") {
        this.search.params = this.search.params.concat(this.compositeCondition())
      } else {
        // 查询参数: 材料名称
        this.search.params.push({
          columnName: this.source == 1 ? 'name' : 'keyword',
          queryType: 'like',
          value: this.queryModel.name
        })
        // 查询参数: 材料类型
        this.search.params.push({
          columnName: 'type',
          queryType: '=',
          value: validatenull(this.queryModel.type.value) ? '' : this.queryModel.type.value
        })
        // 查询参数: 条形码
        this.search.params.push({
          columnName: 'bar_code',
          queryType: 'like',
          value: this.queryModel.barCode
        })
        // 查询参数: 状态
        this.search.params.push({
          columnName: "status",
          queryType: "=",
          value: this.queryModel.status,
        });
        // 查询参数: 是否对外销售
        this.search.params.push({
          columnName: "is_out_sell",
          queryType: "=",
          value: this.queryModel.isOutSell,
        });
      }
      // 数据权限: 材料stuff
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      const functionList = this.source == '1' ? listParentStuffPage : listInstitutionStuffPage
      functionList(this.search).then(responseData => {
        if (responseData.code == 100) {
          this.stuffList = responseData.data.rows
          this.stuffTotal = responseData.data.total
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
        this.getStuffList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getStuffList()
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
      this.getStuffList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getStuffList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = [{ columnName: 'company_id', queryType: '=', value: currentUser.company.id }]
        const functionList = this.source == '1' ? listParentStuffPage : listInstitutionStuffPage
        // 数据权限: 材料stuff
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listStuffRespData, listPermissionRespData] = await Promise.all([
          functionList(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if (listStuffRespData.code == 100 && listPermissionRespData.code == 100) {
          this.stuffTotal = listStuffRespData.data.total
          if (this.source == 1) {
            this.stuffList = listStuffRespData.data.rows.map((item) => {
              item.typeName = item.type.name
              item.natureName = item.nature.name
              item.factoryName = item.factory.name
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              return item
            })
          } else {
            this.stuffList = listStuffRespData.data.rows.map((item) => {
              item.syncNumName = item.syncNum == 1 ? '是' : '否'
              return item
            })
          }

          console.log(this.stuffList[0], '库存');
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'stuffNew:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'stuffNew:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'stuffNew:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'stuffNew:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'stuffNew:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listStuffRespData)
        }
        this.resetLoad()
      } catch (error) {
        this.outputError(error)
      }
    },
    onViewStuff(index, row) {
      this.setLoad()
      getStuffById(row.id).then(responseData => {
        if (responseData.code == 100) {
          this.$refs.synchronousStuffFormRef.$emit('openViewStuffDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateStuff() {
      console.log('添加', this.$refs.synchronousStuffFormRef)
      this.$refs.synchronousStuffFormRef.$emit('openAddStuffDialog')
      // this.$refs.synchronousStuffFormRef.dialogProps.visible = true
    },
    onEditStuff(index, row) {
      this.systemParamConfigSearch.params = [
        {
          columnName: "company_id",
          queryType: "=",
          value: getLocalCurrentCompany().id
        },
        {
          columnName: "id",
          queryType: "=",
          value: row.id
        }
      ]
      console.log(row.id)
      inventory(this.systemParamConfigSearch).then(responseData => {
        if (responseData.code == 100) {
          if (responseData.data.length >= 1) {
            responseData.data.forEach(data => {
              this.inventory = data.stock.surplusStock
              console.log(this.inventory, '可以');
            })

          }
        } else {
          this.showMessage(responseData)
        }
      }).catch(error => {
        this.outputError(error)
      })
      setTimeout(() => {
        if (this.inventory != 0) {
          this.$message({
            message: "物品存在库存，不能修改",
            type: 'warning',
          })
          console.log(this.inventory, '不可以修改')
        } else if (this.inventory == 0) {
          this.setLoad()
          getStuffById(row.id).then(responseData => {
            if (responseData.code == 100) {
              this.$refs.synchronousStuffFormRef.$emit('openEditStuffDialog', responseData.data)
            } else {
              this.showMessage(responseData)
            }
            this.resetLoad()
          }).catch(error => {

            this.outputError(error)
          })
        }
        this.inventory = null
      }, 1000);
    },
    onCopyStuff(index, row) {
      this.setLoad()
      getStuffById(row.id).then(responseData => {
        if (responseData.code == 100) {
          this.$refs.synchronousStuffFormRef.$emit('openCopyStuffDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    }
    ,
    onDeleteStuff(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteStuff(row).then(responseData => {
          if (responseData.code == 100) {
            this.getStuffList()
            this.showMessage({ type: 'success', msg: '删除成功' })
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }).catch(() => {
      })
    }
    ,
    onSortChange(orderby) {
      if (validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getStuffList()
    }
    ,
    initOptions(This) {
      let type_search = {
        params: [{ 'columnName': 'dict_type_id', 'queryType': '=', 'value': '1004462867645374476' }]
      }
      // 响应字段的条件操作符，替换成触发字段的操作符
      type_search.params.forEach(item => {
        if (this.queryTypes[item.columnName]) {
          item.queryType = this.queryTypes[item.columnName]
        }
      })
      // 字段对应表上filter条件
      type_search.params.push.apply(type_search.params, [])
      // 数据权限: 字典项sys_dict_item
      this.pushDataPermissions(type_search.params, this.$route.meta.routerId, '4005')
      this.typeList.splice(0, this.typeList.length)
      listDictItemAll(type_search).then(responseData => {
        this.typeList = responseData.data
      })
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
        this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
      })
    }
  },
  mounted() {
    if (this.source == 1) {
      this.columnList = this.columnListOne
    } else {
      this.columnList = this.columnListTwo
    }
    this.$nextTick(() => {
      this.$on("openSyncStuffDialog", () => {
        this.dialogProps.title = "同步材料信息";
        this.dialogProps.visible = true;
        this.pageInit()
      });
    });
  }
};
</script>
<style scoped lang="scss">
.line {
  margin-top: -10px;
}

.specifications {
  margin-top: -30px;
  width: 100%;
  font-weight: bold;
  padding-bottom: 8px;
  padding: 20px;
  color: #333;
}

.page-container {
  padding: 0;
}

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
    height: calc(100% - 44px) !important;
  }

  .el-table__fixed-right {
    height: 100% !important;
  }
}
</style>

<style>
.stuff_indate .el-dialog__header {
  border-bottom: 1px solid rgb(214, 214, 214) !important;
}
</style>
<style scoped>
/deep/ .el-table__body-wrapper {
  height: calc(100% - 44px) !important;
}
</style>

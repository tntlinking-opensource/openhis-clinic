<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow' ></History>
    <!-- 编辑窗口  -->
    <my-task ref='myTask' :permission='permission' v-on:save-finished='getMyTaskList'></my-task>
    <el-card class="page-container">
        <!--  搜索栏  开始 -->
        <div class='query-form-container'>
          <el-row v-if='!moreCodition' class='search-row'>
            <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm' :inline-message='true'>

            <el-col :span="6">
              <el-form-item label='任务名称' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入任务名称'></el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="6">
              <el-form-item label='是否禁用' prop='isLocked'>
                <el-switch v-model='queryModel.isLocked' active-color='#13ce66' inactive-color='#dbdfe6' active-value='1' inactive-value='0'></el-switch>
              </el-form-item>
            </el-col> -->
            <el-col :span="5">
              <el-form-item label='任务状态' prop='isLocked'>
                  <el-select v-model="queryModel.isLocked" placeholder="请选择任务状态">
                    <el-option
                      v-for="item in isLocked"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            <el-col :span="10" style="text-align:right;padding-right:5px">
              <!-- <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"
                    @click="onCreateClinicOffice()"
                    >添加</el-button
                  >
              </el-button-group> -->
            </el-col>
            </el-form>
          </el-row>
          <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId'  :routerId='$route.meta.routerId' @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
        </div>
        <!--  搜索栏  结束 -->

        <!-- 工具栏 开始 -->

        <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table ref="mutipleTable" height="calc(100vh - 250px)" class='drag_table' :data='clinicOfficeList' border @sort-change='onSortChange' @header-dragend='onChangeWidth' :cell-class-name='cellClassName' :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
            <el-table-column
              label='任务类型'
              prop='taskManagement.tasktype'
              align="center"
            >
            <template slot-scope="scope">
              <span v-if="scope.row.taskManagement.tasktype=='0'">
                常规任务
              </span>
               <span v-else>
                宣传活动
              </span>
            </template>
            </el-table-column>
             <el-table-column
              label='任务名称'
              prop='taskManagement.taskname'
              align="center"
            >

            </el-table-column>
            <el-table-column
              label='任务描述'
              prop='taskManagement.taskdescribe'
              align="center"
              :show-overflow-tooltip="true"
            >

            </el-table-column>
              <el-table-column
              label='发布时间'
              prop='taskManagement.updateDate'
              align="center"
            >

            </el-table-column>
             <el-table-column
              label='任务截至时间'
              prop='taskManagement.taskdeadline'
              align="center"
            >

            </el-table-column>
            <el-table-column
              label='任务状态'
              prop='executeschedule'
              align="center"
            >
            <template slot-scope="scope">
              <span v-if="scope.row.executeschedule=='0'" style="color:red">
                待执行
              </span>
               <span v-else-if="scope.row.executeschedule=='1' & scope.row.auditType=='0'" style="color:orange">
                待审核
              </span>
               <span v-else-if="scope.row.executeschedule=='1' & scope.row.auditType=='1'" style="color:lightgreen">
                 已通过
              </span>
              <span v-else-if="scope.row.executeschedule=='1' & scope.row.auditType=='2'" style="color:red">
                 未通过
              </span>
            </template>
            </el-table-column>

              <!--表行级操作按钮-->
              <el-table-column label='操作' header-align='center' :width='140 + "px"' fixed="right" :key="Math.random()">
                <template slot='header' slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select v-model='columnViews' v-on:save-column-view='saveColumn' v-on:show-all-column='showAllColumn' v-on:show-default-column='showDefaultColumn'></view-columns-select>
                  <export-excel-button v-show='permission.export' :data='clinicOfficeList' :tHeader='getHeads()' :filterVal='getFilterVal()' :plain='true'></export-excel-button>
                </template>
                <template slot-scope='scope'>
                  <!-- <OperationIcon v-show='permission.view' type='info' content='查看' placement='top-start' icon-name='el-icon-view'
                    @click='onViewClinicOffice(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.edit' type='primary' content='编辑' placement='top-start' icon-name='el-icon-edit'
                    @click='onEditClinicOffice(scope.$index, scope.row)'></OperationIcon>
                  <OperationIcon v-show='permission.add' type='primary' content='复制' placement='top-start' icon-name='el-icon-document'
                    @click='onCopyClinicOffice(scope.$index, scope.row)'></OperationIcon>

                  <OperationIcon v-show='permission.view' type='info' content='历史记录' placement='top-start' icon-name='el-icon-info'
                    @click='onShowHistory(scope.$index, scope.row)'></OperationIcon> -->
                  <center>
                    <el-button type="text" v-if="scope.row.executeschedule=='0'"  @click="execute(scope.row,scope.$index)">立即执行</el-button>
                  <el-button type="text" v-if="scope.row.executeschedule=='1'||scope.row.executeschedule=='0'"  @click="viewDetails(scope.row,scope.$index)">查看详情</el-button>
                  </center>
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
            :page-sizes='[20, 50, 100, clinicOfficeTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='clinicOfficeTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </el-card>

      <!-- 立即执行弹出框 -->
      <el-dialog
        title="任务执行"
        :visible.sync="executeDialogVisible"
        v-if="executeDialogVisible"
        width="50%"
        :before-close="handleClose">
        <div class="specifications">任务详情</div>
        <div style="width:80%;margin-left:100px">
           <div>
             任务类型：{{task.taskManagement.tasktype=='0'?"常规任务":"宣传活动"}}
           </div>
           <div>
             任务名称：{{task.taskManagement.taskname}}
           </div>
           <div>
             任务描述：{{task.taskManagement.taskdescribe}}
             <upload-file  :objectId="uploadFiles" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
           </div>
            <div>
             任务发起人：{{task.taskManagement.user.name}}
           </div>
            <div>
             任务截至时间：{{task.taskManagement.taskdeadline}}
           </div>
            <div v-if="task.auditType=='1' || task.auditType=='2'">
             备注：{{task.taskManagement.taskremark}}
           </div>
        </div>
        <br>
        <br>
        <div class="specifications">执行情况反馈</div>
         <div style="width:80%;margin-left:100px">
           <div>
             执行进度：<el-radio :disabled="flagsTask" v-model="executeschedule" label="0">待执行</el-radio>
                        <el-radio :disabled="flagsTask" v-model="executeschedule" label="1">已执行</el-radio>
           </div>

           <div>
             <upload-file-1  :objectId="uploadFilesTask" @getFileList="getFileListTask" :action="flagsTask" @deleteFile="deleteFileTask"></upload-file-1>
           </div>

            <div>
             备注：<el-input :disabled="flagsTask" v-model="remark"></el-input>
           </div>
        </div>
        <span slot="footer" class="dialog-footer">
          <el-button @click="executeDialogVisible = false">取 消</el-button>
          <el-button v-show="!flagsTask" type="primary" @click="save">确 定</el-button>
        </span>
      </el-dialog>
  </el-row>
</template>

<script>
import { validatenull } from '@/utils/validate'
import { listMyTaskPage, saveMyTask, deleteMyTask } from '@/api/myTask/myTask'
import { listResourcePermission } from '@/api/admin/common/permission'
import MyTask from "./myTask.vue"
import ExportExcelButton from '@/components/ExportExcelButton'
import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
import QueryForm from '@/views/components/queryForm'
import MainUI from '@/views/components/mainUI'
import OperationIcon from '@/components/OperationIcon'
import History from '@/views/components/history'
import UploadFile from '../../components/uploadFile.vue'
import UploadFile1 from '../../components/uploadFile.vue'
import { getPhotoById,getFiled } from "@/api/sys/sysSeting"
export default {
  extends: MainUI,
  components: {
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
    MyTask,
    UploadFile,
    UploadFile1
  },
  data() {
    return {
      permission: {
        view: false,
        add: true,
        edit: false,
        remove: false,
        export: false
      },
      executeDialogVisible:false,
      queryTypes: {
        'is_locked': '=',
        'name': 'like',
      },
      queryModel: {
        'isLocked': '',   // 禁用（0：未禁用；1：禁用）
        'name': '',   // 科室名称
      },

       isLocked:[
            {
              value: '0',
              label: '待执行'
            },
            {
              value: '1',
              label: '已执行'
            },
          ],//是否启用


      search: {
        params: [],
        offset: 0,
        limit: 20,
		columnName: '',      // 排序字段名
        order: ''            // 排序
      },
      currentPage: 1,
      clinicOfficeTotal: 0,
      clinicOfficeList: [],


      oprColumnWidth: 140,  // 操作列宽
      tableId: '1026680647570219053',
      schemeId: '1026680647570219069',
      task:{
        taskManagement:{
          tasktype:"",
          taskname:"",
          taskdescribe:"",
          user:{
            name:""
          },
          taskdeadline:"",
        },
      },
       uploadFiles:"",
       uploadFilesTask:"",
        bizFormModel:{
          fileIdFile:[],   //文件
          uploadFile:[],  //文件
          inspectionCheckInfo:{}
        },     //填写报告封装
         bizFormModelTask:{
          fileIdFile:[],   //文件
          uploadFile:[],  //文件
          inspectionCheckInfo:{}
        },     //填写报告封装
        flags:false,
        fileIds:[],
        fileIdsTask:[],
        executeschedule:"0",
        remark:"",
        fileName:'',  //文件名称
        fileNameTask:"",
        flagsTask:false,
    }
  },
  methods: {
    //保存
    save(){
       if(this.flages){
       return
     }
     this.task.executeschedule =this.executeschedule
     this.task.remark = this.remark


     this.flagsTask=true
      console.log(this.bizFormModelTask.fileIdFile,'萨拉时间分厘卡');
    //  let userHeaderFile = new FormData();
      //  userHeaderFile.append("entity", JSON.stringify(this.bizFormModel.inspectionCheckInfo));
      // userHeaderFile.append("deleteIds",JSON.stringify([this.photoId]));
     // return
       this.setLoad()
     if(this.bizFormModelTask.fileIdFile.length>0){
        this.bizFormModelTask.fileIdFile.forEach(item=>{
      //  let userHeaderFile = {};
      let userHeaderFile = new FormData();
         userHeaderFile.append("entity", JSON.stringify(this.task));
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIdsTask));
        userHeaderFile.append("fileIdUploads",item);
        //  userHeaderFile.entity=JSON.stringify(this.bizFormModel.inspectionCheckInfo);
        // userHeaderFile.deleteIds=JSON.stringify([this.photoId]);
        // userHeaderFile.fileIdUploads=item;

      saveMyTask(userHeaderFile).then((res)=>{
          if(res.code=="100"){
            this.$message.success("操作成功!")
            this.uploadFilesTask = ""
            this.getMyTaskList("1")
          }else{
            this.$message.error("保存失败!")
          }
          this.executeDialogVisible=false
          this.resetLoad()
          this.flagsTask=false
      }).catch((error)=>{
        this.$message.error("执行失败，请联系管理员处理!")
        this.flagsTask=false
        this.resetLoad()
      })
      })
     }else{
        let userHeaderFile = new FormData();
         userHeaderFile.append("entity", JSON.stringify(this.task));
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIdsTask));
        userHeaderFile.append("fileIdUploads",this.bizFormModelTask.fileIdFile);

      saveMyTask(userHeaderFile).then((res)=>{
          if(res.code=="100"){
            this.$message.success("操作成功!")
            this.executeDialogVisible=false
            this.flagsTask=false
            this.getMyTaskList("1")
          }
          this.resetLoad()
      }).catch((error)=>{
        this.flagsTask=false
        this.$message.error("执行失败，请联系管理处理!")
        this.resetLoad()
      })
     }
    },

    getPhoto(id) {
      if(!id){
        this.bizFormModel.uploadFile = ''
      }else{
        getFiled(id).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        // let name = res.name.substring(0,res.name.lastIndexOf("."))
        let name = res.name
        this.fileName=name
        this.bizFormModel.uploadFile = src;
        console.log(this.fileName, ".......");
        return src;
      });
      }
    },
    getPhotoTask(id) {
      if(!id){
        this.bizFormModelTask.uploadFile = ''
      }else{
        getFiled(id).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        // let name = res.name.substring(0,res.name.lastIndexOf("."))
        let name = res.name
        this.fileNameTask=name
        this.bizFormModelTask.uploadFile = src;
        return src;
      });
      }
    },
     deleteFile(fileIds){
      this.fileIds.push(fileIds)
      console.log(this.fileIds,'删除看');
    },
    deleteFileTask(fileIds){
      this.fileIdsTask.push(fileIds)
      console.log(this.fileIdsTask,'删除看');
    },
     getFileList(fileList){
      console.log(fileList,'撒娇发生了警方破案就');
      this.bizFormModel.fileIdFile=[]
      this.bizFormModel.uploadFile=[]
      this.bizFormModel.uploadFile=fileList
      for (let i = 0; i < fileList.length; i++) {
       this.bizFormModel.fileIdFile.push(fileList[i].raw)
      }

      console.log(this.bizFormModel.uploadFile,'撒娇发生了警方破案就');
      console.log(this.bizFormModel.fileIdFile,'按法律框架老咔叽分类');
    },
    getFileListTask(fileList){
      console.log(fileList,'撒娇发生了警方破案就111');
      this.bizFormModelTask.fileIdFile=[]
      this.bizFormModelTask.uploadFile=[]
      this.bizFormModelTask.uploadFile=fileList
      for (let i = 0; i < fileList.length; i++) {
       this.bizFormModelTask.fileIdFile.push(fileList[i].raw)
      }

      console.log(this.bizFormModelTask.uploadFile,'撒娇发生了警方破案就222');
      console.log(this.bizFormModelTask.fileIdFile,'按法律框架老咔叽分类333');
    },
    //执行任务
    execute(row,index){
      this.fileIds=[]
      this.fileIdsTask=[]
      this.bizFormModel={
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      }
      this.bizFormModelTask = {
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      }
      this.task = row
      this.flags = true
      this.flagsTask = false
      this.uploadFiles=this.task.taskManagement.id
      this.uploadFilesTask = this.task.id
      console.log(this.uploadFilesTask,'1234567');
      this.photoId=this.task.taskManagement.taskaccessory
      this.photoIdTask = this.task.fileId
      this.executeschedule = this.task.executeschedule
      this.remark = this.task.remark
      this.getPhoto(this.task.taskManagement.taskaccessory);
      this.getPhotoTask(this.task.fileId)
      console.log(this.task);
      this.executeDialogVisible = true
    },
    //查看详情
    viewDetails(row,index){
       this.fileIds=[]
      this.fileIdsTask=[]
      this.bizFormModel={
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      }
      this.bizFormModelTask = {
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      }
      this.task = row
      this.flags = true
      this.flagsTask = true
      this.uploadFiles=this.task.taskManagement.id
      this.uploadFilesTask = this.task.id
      this.photoId=this.task.taskManagement.taskaccessory
      this.photoIdTask = this.task.fileId
      this.executeschedule = this.task.executeschedule
      this.remark = this.task.remark
      this.getPhoto(this.task.taskManagement.taskaccessory);
      this.getPhotoTask(this.task.fileId)
      console.log(this.task);
      this.executeDialogVisible = true
    },
    indexMethod(index){
       return (this.currentPage-1)*this.search.limit+index +1;
    },
    reset(){
      this.$refs.queryForm.resetFields()
      this.onSearch()
    },
    getMyTaskList(val) {
      this.setLoad()
      /* 查询参数 和数据权限 */
      this.search.params = []
       this.search.params.push({
      	  columnName: 'company_id',
      	  queryType: '=',
          value: currentUser.company.id
        })
        console.log(val,'wode');
      if(val=="1") {
        this.search.params = this.search.params.concat(this.compositeCondition())
      }else{
        // 查询参数: 禁用（0：未禁用；1：禁用）
        this.search.params.push({
      	  columnName: 'executeschedule',
      	  queryType: '=',
          value: this.queryModel.isLocked
        })
        // 查询参数: 科室名称
        this.search.params.push({
      	  columnName: 'tm.taskname',
      	  queryType: 'like',
          value: this.queryModel.name
        })
      }
      // 数据权限: 科室clinic_office
      this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
      listMyTaskPage(this.search).then(responseData => {
        if(responseData.code == 100) {
          console.log(responseData,'qiguai');
          this.clinicOfficeTotal = responseData.data.total
          this.clinicOfficeList = responseData.data.rows

        } else {
          this.showMessage(responseData)

        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
        this.resetLoad()
      })
    },
    onSearch() {
      if(this.moreCodition) {
        this.search.offset = 0
        this.currentPage = 1
        this.getMyTaskList()
      } else {
        this.$refs['queryForm'].validate(valid => {
          if (valid) {
            this.search.offset = 0
            this.currentPage = 1
            this.getMyTaskList()
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
      this.getMyTaskList()
    },
    onCurrentChange(val) {
      this.search.offset = (val - 1) * this.search.limit
      this.currentPage = val
      this.getMyTaskList()
    },
    async pageInit() {
      this.setLoad()
      try {
        this.initOptions(this.queryModel)
        this.search.params = []
        // 数据权限: 科室clinic_office
         this.search.params.push({
      	  columnName: 'company_id',
      	  queryType: '=',
          value: currentUser.company.id
        },

        )
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        let [listClinicOfficeRespData, listPermissionRespData] = await Promise.all([
          listMyTaskPage(this.search),
          listResourcePermission(this.$route.meta.routerId)
        ])
        if(listClinicOfficeRespData.code == 100 && listPermissionRespData.code == 100) {
          console.log(listClinicOfficeRespData,'诊所');
          this.clinicOfficeTotal = listClinicOfficeRespData.data.total
          this.clinicOfficeList = listClinicOfficeRespData.data.rows
          this.permission.view = listPermissionRespData.data.find(item => {
            return item.permission === 'myTask:read'
          })
          this.permission.export = listPermissionRespData.data.find(item => {
            return item.permission === 'myTask:export'
          })
          this.permission.add = listPermissionRespData.data.find(item => {
            return item.permission === 'myTask:create'
          })
          this.permission.edit = listPermissionRespData.data.find(item => {
            return item.permission === 'myTask:update'
          })
          this.permission.remove = listPermissionRespData.data.find(item => {
            return item.permission === 'myTask:delete'
          })
        } else {
          this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listClinicOfficeRespData)
        }
        this.resetLoad()
      } catch(error) {
        this.outputError(error)
      }
    },
    onViewClinicOffice(index, row) {
      this.setLoad()
      getClinicOfficeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.myTask.$emit('openViewClinicOfficeDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCreateClinicOffice() {
      this.$refs.myTask.$emit('openAddClinicOfficeDialog')
    },
    onEditClinicOffice(index, row) {
      this.setLoad()
      getClinicOfficeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.myTask.$emit('openEditClinicOfficeDialog', responseData.data)
        }else{
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onCopyClinicOffice(index, row) {
      this.setLoad()
      getClinicOfficeById(row.id).then(responseData => {
        if(responseData.code == 100) {
          this.$refs.myTask.$emit('openCopyClinicOfficeDialog', responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    onDeleteClinicOffice(index, row) {
      this.$confirm('确定删除吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.setLoad()
        deleteClinicOffice(row).then(responseData => {
          if(responseData.code == 100) {
            this.getMyTaskList()
            this.showMessage({type: 'success', msg: '删除成功'})
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }).catch(() => {})
    },
    onSortChange( orderby ) {
      if(validatenull(orderby.prop)) {
        this.search.columnName = ''
        this.search.order = ''
      } else  {
        this.search.columnName = orderby.prop
        this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
      }

      this.getMyTaskList()
    },
    initOptions(This) {
    }
  },
  watch: {
    clinicOfficeList(val){
      if(val){
        this.$nextTick(() => {
            this.$refs.mutipleTable.doLayout();
        });
      }
    }
  },
  updated(){
    if(this.$refs.mutipleTable){
       this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.mutipleTable.doLayout()// 对 Table 进行重新布局
        })
    }
  },
  mounted() {
    this.pageInit()
  }
}
</script>
<style lang="scss" scoped>
.page-container{
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
 /deep/.el-table__body-wrapper {
  //解决数据展示超出body高度不滚动bug
  overflow-y: auto;
   // 减去的是表格header的高度
   height: calc(100% - 44px) !important;
 }

 .el-table__fixed-right {
      height: 100% !important;
  }
}
.specifications {
  width: 100%;
  font-weight: bold;
  font-size: 15px;
  padding-bottom: 8px;
  margin-top: -20px;
  //padding: 20px;
  color: #333;
}
</style>
<style scoped>
/deep/ .el-table__body-wrapper{
    height: calc(100% - 44px) !important;
  }
</style>

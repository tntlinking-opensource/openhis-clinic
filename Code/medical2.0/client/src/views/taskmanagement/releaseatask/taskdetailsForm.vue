<template>
    <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    v-if="dialogProps.visible"
    :close-on-click-modal="false"
    width="60%"
    ref="taskdetailsForm"

    v-loading="loading"
  >
  <!-- 选择任务执行审核 -->
    <div v-if="mark=='0'">
          <el-table
            :data="tableData"
            style="width: 100%"
            max-height="250">
            <el-table-column
               type="index"
               label='序号'
               width="width"
               align="center"
            >
            </el-table-column>
            <el-table-column
              prop="company.name"
              label="执行诊所"
              align="center"
              width="width">
            </el-table-column>
            <el-table-column
              prop="executeschedule"
              label="执行状态"
              align="center"
              width="width">
              <template slot-scope="scope">
                <span v-if="scope.row.executeschedule=='0'" style="color:orange;">
                  待执行
                </span>
                <span v-if="scope.row.executeschedule=='1'" style="color:lightgreen;">
                  已执行
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="updateBy"
              label="执行人"
              align="center"
              width="width">
            </el-table-column>
            <el-table-column
              prop="updateDate"
              label="执行时间"
              align="center"
              width="width">
            </el-table-column>
            <el-table-column
              prop="auditType"
              label="审核状态"
              align="center"
              width="width">
              <template slot-scope="scope">
                <span v-if="scope.row.auditType=='0'" style="color:orange">
                    待审核
                </span>
                 <span v-if="scope.row.auditType=='1'" style="color:lightgreen">
                    已审核
                </span>
                <span v-if="scope.row.auditType=='2'" style="color:red">
                    未通过
                </span>
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              label="操作"
              width="120">
              <template slot-scope="scope">
                <el-button
                  v-if="scope.row.auditType=='0'"
                  @click.native.prevent="auditStart(scope.$index, scope.row)"
                  type="text"
                  size="small">
                  审核
                </el-button>
                 <el-button
                  v-if="scope.row.auditType==='1'||scope.row.auditType==='2'"
                  @click.native.prevent="check(scope.$index, scope.row)"
                  type="text"
                  size="small">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
    </div>
  <div v-else>
  <div>
     <div class="specifications">任务详情</div>
        <div style="width:80%;margin-left:100px">
           <div>
             任务类型：{{this.indexdata.tasktype=='0'?"常规任务":"宣传活动"}}
           </div>
           <div>
             任务名称：{{this.indexdata.taskname}}
           </div>
           <div>
             任务描述：{{this.indexdata.taskdescribe}}
              <upload-file  :objectId="uploadFiles" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
           </div>
            <div>
             任务发起人：{{this.indexdata.user.name}}
           </div>
            <div>
             任务截至时间：{{this.indexdata.taskdeadline}}
           </div>
        </div>
         <br>
        <br>
        <div class="specifications">执行情况反馈</div>
         <div style="width:80%;margin-left:100px">
           <div>
             执行进度：{{this.feedbacklist[0]==undefined?"":this.feedbacklist[0].executeschedule}}
           </div>

           <div>
              <upload-file-1  :objectId="uploadFilesfee" @getFileList="getFileListfee" :action="flagsfee" @deleteFile="deleteFile"></upload-file-1>
           </div>

            <div>
             备注：{{this.feedbacklist[0]==undefined?"---":this.feedbacklist[0].remark}}
           </div>
        </div>
<!-- <el-descriptions title="任务详情" :column="1" >
    <el-descriptions-item label="任务类型">{{this.indexdata.tasktype=this.indexdata.tasktype=="0"?"常规任务":"宣传活动"}}</el-descriptions-item>
    <el-descriptions-item label="任务名称">{{this.indexdata.taskname}}</el-descriptions-item>
    <el-descriptions-item label="任务描述">{{this.indexdata.taskdescribe}}</el-descriptions-item>
    <el-descriptions-item label="任务发起人">{{this.indexdata.taskinitiatorname}}</el-descriptions-item>
    <el-descriptions-item label="任务截止时间">{{this.indexdata.taskdeadline}}</el-descriptions-item>
    <el-descriptions-item >
    <upload-file  :objectId="uploadFiles" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
    </el-descriptions-item>
</el-descriptions> -->


<!-- <el-descriptions title="执行情况反馈" :column="1" >
    <el-descriptions-item label="执行进度">{{this.zxjd}}</el-descriptions-item>

    <el-descriptions-item>
      <upload-file-1  :objectId="uploadFilesfee" @getFileList="getFileListfee" :action="flagsfee" @deleteFile="deleteFile"></upload-file-1>
    </el-descriptions-item>


    <el-descriptions-item label="备注">{{this.feedbacklist[0]==undefined?"---":this.feedbacklist[0].remark}}</el-descriptions-item>
</el-descriptions> -->

<el-descriptions title="审核结果" ></el-descriptions>
    <el-form  @open="onDialogOpen()" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
      <el-form-item label="审核结果" prop="auditresult">
    <el-radio-group :disabled="checkFlage" v-model="ruleForm.auditresult">
      <el-radio :label="'0'">通过</el-radio>
      <el-radio :label="'1'">不通过</el-radio>
    </el-radio-group>
  </el-form-item>
  <el-form-item label="备注" >
    <el-input :disabled="checkFlage" type="textarea" v-model="ruleForm.taskremark"></el-input>
  </el-form-item>
    </el-form>



      </div>
    <span slot='footer' class='dialog-footer'>
      <el-button :disabled="flage" type='primary' v-if="!checkFlage" :plain='true' @click="submitForm('ruleForm')">保 存</el-button>
      <el-button :plain='true' type="primary" @click='onDialogClose1()'>取 消</el-button>
    </span>
  </div>
    </el-dialog>
</template>


<script>
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import VDistpicker from 'v-distpicker';
import {inserttask,getusertree,getfeedbacktable,updateaudit} from "@/api/taskmanagement/taskmanagement"
import UploadFile from '../../components/uploadFile.vue'
import UploadFile1 from '../../components/uploadFile.vue'
import { getPhotoById,getFiled } from "@/api/sys/sysSeting"
import { getjglist} from "@/api/toll/tollInfo";
import { listMyTaskPage, saveMyTask, updateMyTask } from '@/api/myTask/myTask'
  export default {
    extends: BaseUI,
  name: "taskdetails-form",
  components: {
    OperationIcon,
    VDistpicker,
    UploadFile,
    UploadFile1
  },
   props:['closeValue'],
   data(){
     return{
       indexdata:[],
       dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      gettaskrcvo: {
        taskmanagementid:'',
      },
      feedbacklist:[],
      ruleForm:{
        id:'',
        auditresult:"0",
        taskremark:'',
      },
       rules: {
          auditresult: [
            { required: true, message: '请选择审核结果', trigger: 'change' }
          ],
       },
       fileName:'',  //文件名称
       bizFormModel:{
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      },
      bizFormModel2:{
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      },
      flags:true,
      flagsfee:true,
      fileIds:[],
      flages:false,  //防止重复提交
      uploadFiles:'',
      taskaccessoryid:'',//附件id
      uploadFilesfee:'',
      zxjd:'',
      bz:'',
      mark:"0",  //div切换标志
      tableData:[],  //执行任务数据
      checkFlage:false,
      taskManagementId:"",
      myTaskId:"",
     }
   },
   methods:{
     //取消按钮
     onDialogClose() {
     this.$emit('typeclick',"");
      this.dialogProps.visible = false;

    },
    onDialogClose1() {
      this.mark="0";
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["ruleForm"].clearValidate();
      });
    },

    submitForm(){
      updateaudit(this.ruleForm).then((responseData)=>{
        if(responseData.code == 100){
          if(this.ruleForm.auditresult=='0'){
            this.myTaskId.auditType = "1"
          }else{
            this.myTaskId.auditType = "2"
          }
          updateMyTask(this.myTaskId).then(res=>{
              if(res.code==100){
                this.ruleForm={
                  id:'',
                  auditresult:0,
                  taskremark:'',
                };
                this.getFeedbacktableList(this.taskManagementId)
                this.onDialogClose1();
                this.$message.success("审核成功")
              }
          })
        }
      })
    },
    //预览
    look(row){

      //  var blob = this.dataURLtoBlob(row)
      //   const elink = document.createElement('a')
      //   // 设置下载文件名
      //   const timedate = Date.parse(new Date())
      //   elink.download = this.fileName
      //   elink.style.display = 'none'
      //   elink.href = URL.createObjectURL(blob)
      //   document.body.appendChild(elink)
      //   elink.click()
      //   document.body.removeChild(elink)
     window.open("http://192.168.0.31:8099/ffview/onlinePreview?url=" + encodeURIComponent(Base64.encode(row)));
    },
    // 将base64转换为blob
    dataURLtoBlob: function (dataurl) {
      var arr = dataurl.split(',')
      var mime = arr[0].match(/:(.*?);/)[1]
      var bstr = atob(arr[1])
      var n = bstr.length
      var u8arr = new Uint8Array(n)
      while (n--) {
        u8arr[n] = bstr.charCodeAt(n)
      }
      return new Blob([u8arr], { type: mime })
    },
    getfileddata(){
      getFiled(this.taskaccessory).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        // let name = res.name.substring(0,res.name.lastIndexOf("."))
        let name = res.name
        this.fileName=name
        this.bizFormModel.uploadFile = src;
        console.log(this.fileName, ".......");
        return src;
      });
    },
    getfileddata2(){
      getFiled(this.feedbacklist.fileid).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        // let name = res.name.substring(0,res.name.lastIndexOf("."))
        let name = res.name
        this.fileName=name
        this.bizFormModel2.uploadFile = src;
        console.log(this.fileName, ".......");
        return src;
      });
    },
    getFileList(fileList){
      console.log(fileList,'撒娇发生了警方破案就');
      this.bizFormModel.fileIdFile=[]
      this.bizFormModel.uploadFile=[]
      this.bizFormModel.uploadFile=fileList
      if(fileList!=null&&fileList!=undefined){
        for (let i = 0; i < fileList.length; i++) {
       this.bizFormModel.fileIdFile.push(fileList[i].raw)
      }
      }

      console.log(this.bizFormModel.uploadFile,'撒娇发生了警方破案就');
      console.log(this.bizFormModel.fileIdFile,'按法律框架老咔叽分类');
    },
    getFileListfee(fileList){
      console.log(fileList,'撒娇发生了警方破案就');
      this.bizFormModel2.fileIdFile=[]
      this.bizFormModel2.uploadFile=[]
      this.bizFormModel2.uploadFile=fileList

      if(fileList!=null&&fileList!=undefined){
        for (let i = 0; i < fileList.length; i++) {
       this.bizFormModel.fileIdFile.push(fileList[i].raw)
      }
      }
      console.log(this.bizFormModel2.uploadFile,'撒娇发生了警方破案就');
      console.log(this.bizFormModel2.fileIdFile,'按法律框架老咔叽分类');
    },
    deleteFile(fileIds){
      this.fileIds.push(fileIds)
      console.log(this.fileIds,'删除看');
    },
    getFeedbacktableList(row){
      console.log(row,'获取自豪');
      let search = {
        params: [
          {
            columnName: "tm.id",
            queryType: "=",
            value: row.id,
          },
          {
            columnName: "taskmanagement_id",
            queryType: "=",
            value: row.id,
          }
        ],
        offset: 0,
        limit: 2000,
        columnName: "", // 排序字段名
        order: "", // 排序
      }
      listMyTaskPage(search).then((res)=>{
        if(res.code==100){

          this.tableData = res.data.rows
          console.log(this.tableData,"nishi");
        }
      }).catch((error)=>{

      })
    },
    check(index,row){
       this.feedbacklist=[]
       this.uploadFiles=row.taskManagement.id

        this.taskaccessory=row.taskManagement.taskaccessory
        this.indexdata=row.taskManagement;
        this.gettaskrcvo.taskmanagementid=row.taskManagement.id
         this.ruleForm.id=row.taskManagement.id
        this.getfileddata();
       // this.Getfeedbackdata();
         this.feedbacklist.push({
                companyId:row.companyId,
                createdBy:row.createdBy,
                createdDate:row.createdDate,
                executeschedule:row.executeschedule == 1 ? "已完成":"进行中",
                fileid:row.fileid,
                id:row.id,
                remark:row.remark?row.remark:"",
                taskmanagementid:row.taskManagement.id,
                updatedBy:row.updatedBy,
                updatedDate:row.updatedDate,
                auditType:row.auditType
              })
        this.ruleForm.auditresult = row.taskManagement.auditresult
        this.ruleForm.taskremark = row.taskManagement.taskremark
        this.uploadFilesfee=this.feedbacklist[0].id
        this.getfileddata2();
        this.checkFlage=true
        this.mark = "1"
    },
    auditStart(index,row){
      this.checkFlage = false
      this.feedbacklist=[]
      this.myTaskId = row
       this.uploadFiles=row.taskManagement.id

        this.taskaccessory=row.taskManagement.taskaccessory
        this.indexdata=row.taskManagement;
        this.gettaskrcvo.taskmanagementid=row.taskManagement.id
         this.ruleForm.id=row.taskManagement.id
        this.getfileddata();
       // this.Getfeedbackdata();
         this.feedbacklist.push({
                companyId:row.companyId,
                createdBy:row.createdBy,
                createdTime:row.createdDate,
                executeschedule:row.executeschedule == 1 ? "已完成":"进行中",
                fileid:row.fileid,
                id:row.id,
                remark:row.remark?row.remark:"",
                taskmanagementid:row.taskManagement.id,
                updatedBy:row.updatedBy,
                updatedTime:row.updatedDate,
              })
        this.uploadFilesfee=this.feedbacklist[0].id
        this.getfileddata2();
        this.mark = "1"
    }
   },
    mounted: function(){
       this.$nextTick(() => {
      this.$on('openAddworkbenchDialog', function(types) {
        this.mark = "0"
        this.getFeedbacktableList(types)
        this.taskManagementId = types
        this.dialogProps.action = 'add'
        this.dialogProps.title = ''
        this.tabIndex = '1'
        this.dialogProps.visible = true
      })
    });

    }

  }
  </script>

  <<style lang="scss" scoped>
  .el-descriptions--mini:not(.is-bordered) .el-descriptions-item__cell{
    padding-bottom: 13px;
    padding-left: 16%;
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

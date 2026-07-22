<template>
<el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    v-if="dialogProps.visible"
    :close-on-click-modal="false"
    width="60%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
<div>
  <el-form ref="addtaskForm" :model="ruleForm" :rules="rules"  label-width="20%" class="demo-ruleForm">
      <el-form-item label="任务类型" prop="tasktype">
    <el-radio-group v-model="ruleForm.tasktype">
      <el-radio :label="'0'">常规任务</el-radio>
      <el-radio :label="'1'">宣传活动</el-radio>
    </el-radio-group>
  </el-form-item>
  <el-form-item label="任务名称" prop="taskname">
    <el-input v-model="ruleForm.taskname"></el-input>
  </el-form-item>
  <el-form-item label="任务描述" prop="taskdescribe">
    <el-input
  type="textarea"
  :rows="3"
  placeholder="请填写任务描述"
  v-model="ruleForm.taskdescribe">
</el-input>
<!-- <el-upload
  class="upload-demo"
  action="https://jsonplaceholder.typicode.com/posts/"
  :on-preview="handlePreview"
  :on-remove="handleRemove"
  :before-remove="beforeRemove"
  multiple
  :limit="10"
  :on-exceed="handleExceed"
  :file-list="fileList">
  <el-button size="small" type="primary">点击上传</el-button>
  <div slot="tip" class="el-upload__tip">只能上传jpg/png文件,且不超过500kb</div>
</el-upload> -->
              <div style="margin-left:-50px">
              <upload-file  :objectId="uploadFiles" :value="uploads" @getFileList="getFileList" :action="flags" @deleteFile="deleteFile"></upload-file>
              </div>
  </el-form-item>
  <el-form-item label="任务发起人">
      {{this.ruleForm.taskinitiator}}
    <!-- <el-descriptions>
    <el-descriptions-item label="">{{this.form.initiator}}</el-descriptions-item>
</el-descriptions> -->
  </el-form-item>
  <el-form-item label="任务截止时间"  prop="taskdeadline">
    <el-col :span="11">
      <!-- <el-date-picker type="datetime" placeholder="选择日期时间" v-model="ruleForm.taskdeadline" style="width: 100%;"></el-date-picker> -->
      <el-date-picker
      v-model="ruleForm.taskdeadline"
      type="datetime"
      value-format="yyyy-MM-dd HH:mm:ss"
      placeholder="选择日期时间">
    </el-date-picker>
    </el-col>
    <!-- <el-col class="line" :span="2">-</el-col>
    <el-col :span="11">
      <el-time-picker placeholder="选择时间" v-model="ruleForm.taskdeadline2" style="width: 100%;"></el-time-picker>
    </el-col> -->
  </el-form-item>

  <el-form-item label="任务执行人" width="80%">
    <!-- <el-select v-model="ruleForm.taskregion" placeholder="请选择任务执行人">
      <el-option label="诊所管理员" value="1247793999298339926"></el-option>
      <el-option label="执行人二" value="beijing"></el-option>
    </el-select> -->
    <!-- <el-cascader
    :options="ruleForm.taskregion"
    :props="{ multiple: true, checkStrictly: true }"
    clearable></el-cascader> -->
    <el-select v-model="jgiddatalist" multiple placeholder="请选择" @change="hideOther()">
      <el-option
      key="-1"
          label="全部"
          value="qb"
          ></el-option>
    <el-option
          v-for="(item, i) in jglist"
          :key="i"
          :label="item.jgmc"
          :value="item.jgid"
        />
  </el-select>
  </el-form-item>
  
</el-form>
  
</div>
<span slot='footer' class='dialog-footer'>
      <el-button :disabled="flag" type='primary' :plain='true' @click="save('addtaskForm')">保 存</el-button>
  
      <el-button :disabled="flag" type='primary' :plain='true' @click="submitForm('addtaskForm')">发 布</el-button>
      
      <el-button :plain='true' type="primary" @click='onDialogClose()'>取 消</el-button>
    </span> 
</el-dialog>
</template>

<style>
  .el-table .warning-row {
    background: oldlace;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
</style>

<script>
import BaseUI from "@/views/components/baseUI";
import {inserttask,getusertree,storageTask} from "@/api/taskmanagement/taskmanagement"
import UploadFile from '../../components/uploadFile.vue'
import { getPhotoById,getFiled } from "@/api/sys/sysSeting"
import { getjglist} from "@/api/toll/tollInfo";
  export default {
    extends: BaseUI,
  name: "addtask-form",
  components: {
    UploadFile
  },  
   props:['closeValue'],
    data() {
      return {
        tableData: [],
        dialogProps: {
        visible: false,
        action: '',
        title: '',
      },
      ruleForm: {
          tasktype:0,
          taskname:'',
          taskdescribe:'',
          taskaccessory:'',
          taskinitiator:currentUser.name,
          taskdeadline:'',
          taskregion:[],
          
        //   name: '',
        //   describe:'',
        //   initiator:currentUser.name,
        //   region: '',
        //   date1: '',
        //   date2: '',
        //   delivery: false,
        //   type: [],
        //   resource: '',
        //   desc: '',
        //   radio:1,
        },
        fileList: [{name: 'food.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'}, {name: 'food2.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'}],

        rules: {
            tasktype: [
            { required: true, message: '请选择任务类型', trigger: 'change' }
          ],
          taskname: [
            { required: true, message: '请填写任务名称', trigger: 'blur' },
            // { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ],
          taskdescribe: [
            { required: true, message: '请填写任务描述', trigger: 'change' }
          ],
          
          taskdeadline: [
            {  required: true, message: '请选择日期时间', trigger: 'change' }
          ],
          taskregion: [
            { type: 'array', required: true, message: '请选择任务执行人', trigger: 'change' }
          ],
        //   resource: [
        //     { required: true, message: '请选择活动资源', trigger: 'change' }
        //   ],
        //   desc: [
        //     { required: true, message: '请填写活动形式', trigger: 'blur' }
        //   ]
        },
        bizFormModel:{
        fileIdFile:[],   //文件
        uploadFile:[],  //文件
        inspectionCheckInfo:{}
      },     //填写报告封装
      flags:false,
      flags:false,  //防止重复提交
      gettaskrcvo: {
          limit: 20,
        offset: 0,
        jgid: '',
          kssj:'',
          jssj:'',
          tasktype:'',
          taskstatus:'',
          taskname:'',
          companyId:currentUser.company.id,
        },
        jglist:[],
        YpjxcRc:{
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        orderby:"batch_no",
        jgid:null,
      },
      jgiddatalist:[],
      fileIds:[],
      uploadFiles:"",
      taskRow:null, //任务对象
      uploads:null,
      }
    },
    methods: {

      //任务保存
      save(formName){
           if(this.flags){
       return
     }
     this.flags=true
       this.$refs[formName].validate(async (valid) => {
         if(valid){
            let countId ="0"
       if(this.bizFormModel.fileIdFile.length>0){
         for(let item of this.bizFormModel.fileIdFile){
         //this.bizFormModel.fileIdFile.forEach( item=>{
      //  let userHeaderFile = {};
    
      let userHeaderFile = new FormData();
        if(this.taskRow==null){
            this.ruleForm.taskinitiator=currentUser.id;
           userHeaderFile.append("entity", JSON.stringify(this.ruleForm));
        }else{
        this.taskRow.taskname = this.ruleForm.taskname
        this.taskRow.taskdescribe = this.ruleForm.taskdescribe
        this.taskRow.taskaccessory = this.ruleForm.taskaccessory
        this.taskRow.taskinitiator = currentUser.id
        this.taskRow.taskdeadline = this.ruleForm.taskdeadline
        this.taskRow.taskregion = this.ruleForm.taskregion
         userHeaderFile.append("entity", JSON.stringify(this.taskRow));
          
        }
     
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",item);
        userHeaderFile.append("countId",countId);
      // this.$refs[formName].validate((valid) => {
         //  if (valid) {
           await  storageTask(userHeaderFile).then((responseData)=>{
               if(responseData.code === 100){
                 countId = responseData.data.id
               }else {
                 this.$message.error("执行失败！")
                  
                 this.flags=false
                 return;
               }
             })
          //  }else{
          //    console.log("失败");
          //    this.flags=false
          //  }
         //})
         }
            this.ruleForm= {
                tasktype:'0',
                taskname:'',
                taskdescribe:'',
                taskaccessory:'',
                taskinitiator:currentUser.name,
                taskdeadline:'',
                taskregion:[],
             };
            this.$message.success("执行成功！")
            this.flags=false
            this.onDialogClose();
        //})
       }else{

          let userHeaderFile = new FormData();
        
          if(this.taskRow==null){
            this.ruleForm.taskinitiator=currentUser.id;
           userHeaderFile.append("entity", JSON.stringify(this.ruleForm));
        }else{
          this.taskRow.taskname = this.ruleForm.taskname
          this.taskRow.taskdescribe = this.ruleForm.taskdescribe
          this.taskRow.taskaccessory = this.ruleForm.taskaccessory
          this.taskRow.taskinitiator = currentUser.id
          this.taskRow.taskdeadline = this.ruleForm.taskdeadline
          this.taskRow.taskregion = this.ruleForm.taskregion
          userHeaderFile.append("entity", JSON.stringify(this.taskRow));
        }
 
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",null);
        userHeaderFile.append("countId",countId);
       // this.$refs[formName].validate((valid) => {
         //  if (valid) {
            //  this.ruleForm.taskinitiator=currentUser.id;
             storageTask(userHeaderFile).then((responseData)=>{
               if(responseData.code === 100){
                 this.ruleForm= {
                    tasktype:0,
                    taskname:'',
                    taskdescribe:'',
                    taskaccessory:'',
                    taskinitiator:currentUser.name,
                    taskdeadline:'',
                    taskregion:[],
                          };
                 this.flags=false
                 this.onDialogClose();
               }
             })
          //  }else{
          //    console.log("失败");
          //    this.flags=false
          //  }
        // })
       }
         }
       })

     
     
      },

      tableRowClassName({row, rowIndex}) {
        if (rowIndex === 1) {
          return 'warning-row';
        } else if (rowIndex === 3) {
          return 'success-row';
        }
        return '';
      },
      onDialogClose() {
     this.$emit('typeclick',"");
      this.dialogProps.visible = false;
      
    },
    onDialogOpen() {
      this.$nextTick(() => {
        this.$refs["addtaskForm"].clearValidate();
      });
    },
    //附件上传
    handleRemove(file, fileList) {
      },
      handlePreview(file) {
      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },
      submitForm(formName){
       
        if(this.flags){
       return
     }
     this.flags=true
     this.$refs[formName].validate( async (valid) => {
       if(valid){
         let countId = "0"
       if(this.bizFormModel.fileIdFile.length>0){
         for(let item of this.bizFormModel.fileIdFile){
       // this.bizFormModel.fileIdFile.forEach(item=>{
      //  let userHeaderFile = {};
      
     
      let userHeaderFile = new FormData();
       if(this.taskRow==null){
            this.ruleForm.taskinitiator=currentUser.id;
           userHeaderFile.append("entity", JSON.stringify(this.ruleForm));
        }else{
        this.taskRow.taskname = this.ruleForm.taskname
        this.taskRow.taskdescribe = this.ruleForm.taskdescribe
        this.taskRow.taskaccessory = this.ruleForm.taskaccessory
        this.taskRow.taskinitiator = currentUser.id
        this.taskRow.taskdeadline = this.ruleForm.taskdeadline
        this.taskRow.taskregion = this.ruleForm.taskregion
         userHeaderFile.append("entity", JSON.stringify(this.taskRow));    
        }
      //   userHeaderFile.append("entity", JSON.stringify(this.ruleForm));
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",item);
        userHeaderFile.append("countId",countId);
       // this.$refs[formName].validate((valid) => {
         //  if (valid) {
            //  this.ruleForm.taskinitiator=currentUser.id;
            await inserttask(userHeaderFile).then((responseData)=>{
               if(responseData.code === 100){
                 countId = responseData.data.id
               }else{
                this.flags=false
                this.$message.error("执行失败！")
                return;
               }
             })

          //  }else{
          //    console.log("失败");
          //    this.flags=false
          //  }
       //  })
       // })
         }
          this.ruleForm= {
                tasktype:0,
                taskname:'',
                taskdescribe:'',
                taskaccessory:'',
                taskinitiator:currentUser.name,
                taskdeadline:'',
                taskregion:[],
            };
            this.$message.success("执行成功！")
            this.flags=false
            this.onDialogClose();

       }else{
         
           
      let userHeaderFile = new FormData();
       if(this.taskRow==null){
            this.ruleForm.taskinitiator=currentUser.id;
           userHeaderFile.append("entity", JSON.stringify(this.ruleForm));
        }else{
        this.taskRow.taskname = this.ruleForm.taskname
        this.taskRow.taskdescribe = this.ruleForm.taskdescribe
        this.taskRow.taskaccessory = this.ruleForm.taskaccessory
        this.taskRow.taskinitiator = currentUser.id
        this.taskRow.taskdeadline = this.ruleForm.taskdeadline
        this.taskRow.taskregion = this.ruleForm.taskregion
         userHeaderFile.append("entity", JSON.stringify(this.taskRow));
          
        }
        userHeaderFile.append("deleteIds",JSON.stringify(this.fileIds));
        userHeaderFile.append("fileIdUploads",null);
        userHeaderFile.append("countId",countId);
       // this.$refs[formName].validate((valid) => {
          // if (valid) {
            //  this.ruleForm.taskinitiator=currentUser.id;
             inserttask(userHeaderFile).then((responseData)=>{
               if(responseData.code === 100){
                 this.ruleForm= {
          tasktype:'0',
          taskname:'',
          taskdescribe:'',
          taskaccessory:'',
          taskinitiator:currentUser.name,
          taskdeadline:'',
          taskregion:[],
                 };
                 this.flags=false
                 this.onDialogClose();
               }
             })
          //  }else{
          //    console.log("失败");
          //    this.flags=false
          //  }
         //})
       }
       }
     })
      
        
      },

       getFileList(fileList){
      this.bizFormModel.fileIdFile=[]
      this.bizFormModel.uploadFile=[]
      this.bizFormModel.uploadFile=fileList
      for (let i = 0; i < fileList.length; i++) {
       this.bizFormModel.fileIdFile.push(fileList[i].raw)
      }
     
    },
    deleteFile(fileIds){
      this.fileIds.push(fileIds)
    },
    Getusertreedata(){
      getusertree(this.gettaskrcvo).then((responseData)=>{
        if(responseData.code === 100 ){
          this.ruleForm.taskregion=responseData.data;
        }
      })
    },
    Getcliniclist(type){
          this.jglist=[];
          getjglist(this.YpjxcRc).then((responseData)=>{
              if (responseData.code === 100){
                  if(responseData.data.length>0){
                     this.zsids='';
                      responseData.data.forEach((item)=>{
                          if(item.jgid!==currentUser.company.id){
                          this.jglist.push({
                              jgid:item.jgid,
                              jgmc:item.jgmc,
                           })
                          
                         }
                      })
                     
                  }
                  if(type===2){
                       this.jgiddatalist = []
                        if(this.jglist.length===this.taskRow.taskexecutors.length){
                          this.jgiddatalist.push("qb")
                        }else{
                          this.taskRow.taskexecutors.forEach((item)=>{
                            this.jgiddatalist.push(item.company.id)
                        })
                        }
                      this.uploadFiles= this.taskRow.id 
                  }
              }
          })
      },
      hideOther() {
      // 数组的indexOf方法，会返回当前值在数组中的下标，没有则为-1
      if (this.jgiddatalist.indexOf('qb') === -1 ) {
        this.jglist.map((item) => {
          item.disabled = true
          
        })
        
      } else {
        this.jglist.map((item) => {
          item.disabled = false
        })
        this.jgiddatalist = ['qb']
       }
      
      if(this.jgiddatalist==="qb"){
this.ruleForm.taskregion=[];
        this.jglist.forEach((item2)=>{
         this.ruleForm.taskregion.push(item2.jgid)
                      })
      }
      else{
        this.ruleForm.taskregion=[];
        this.ruleForm.taskregion=this.jgiddatalist;
      }
    },
      init(){
        this.ruleForm = {
          tasktype:'0',
          taskname:'',
          taskdescribe:'',
          taskaccessory:'',
          taskinitiator:currentUser.name,
          taskdeadline:'',
          taskregion:[],
        }
        this.jgiddatalist=[]
        this.taskRow=null
      },
      openAddworkbenchDialog(types) {
        let titles = "";
        this.uploadFiles = ""
        this.uploads = []
        this.bizFormModel = {
          fileIdFile: [],
          uploadFile: [],
          inspectionCheckInfo: {}
        }
        this.init()
        this.flags = false
        this.flags = false
        if (types === "") { titles = "新增" } else if (types !== "") { titles = "修改" } else { titles = "" };
        if (types !== "" && types != null) {
          this.Getselectmbbm(types);
        } else {
          this.mblx = "0",
          this.bllx = "0",
          this.formLabelAlign = {};
        }
        this.Getcliniclist(1)
        this.dialogProps.action = 'add'
        this.dialogProps.title = '新增任务信息'
        this.tabIndex = '1'
        this.dialogProps.visible = true
        this.province = ''
        this.city = ''
        this.area = ''
      },
      openUpdateworkbenchDialog(row) {
        this.uploadFiles = ""
        let titles = "";
        this.taskRow = row
        this.fileIds = []
        this.bizFormModel = {
          fileIdFile: [],
          uploadFile: [],
          inspectionCheckInfo: {}
        }
        this.uploads = []
        this.flags = false
        this.flags = false
        if (row === "") { titles = "新增" } else if (row !== "") { titles = "修改" } else { titles = "" };
        if (row !== "" && row != null) {
        } else {
          this.mblx = "0",
          this.bllx = "0",
          this.formLabelAlign = {};
        }
        this.Getcliniclist(2)
        let arr = []
        row.taskexecutors.forEach((item) => {
          arr.push(item.id)
        })
        this.ruleForm = {
          tasktype: row.tasktype,
          taskname: row.taskname,
          taskdescribe: row.taskdescribe,
          taskaccessory: row.taskaccessory,
          taskinitiator: row.taskinitiatorname,
          taskdeadline: row.taskdeadline,
          taskregion: arr,
        }
        this.dialogProps.action = 'update'
        this.dialogProps.title = '修改任务信息'
        this.tabIndex = '1'
        this.dialogProps.visible = true
      },
    },
  }
</script>
</script>
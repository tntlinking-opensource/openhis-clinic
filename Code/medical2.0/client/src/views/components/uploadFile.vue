<template>
  <div>
    <el-collapse v-model="activeNames" v-loading="loading">
      <el-collapse-item name="3">
        <template slot="title">
          <span class="collapse-title">{{title}}<span style="color: #ccc">（展开查看更多）</span></span>
          <div class="collapse-btn">
<!--            <el-button style="width: 90px;" type="primary" @click.stop="onUploadFile" v-if='!action'>上传</el-button>-->
            <el-button style="width: 90px;" type="primary" @click.stop="onUploadFile" v-if='authority == "add" || authority == "edit"'>上传</el-button>
          </div>
        </template>
        <el-upload
          :disabled='authority == "view"'
          class="upload-demo"
          multiple
          action=""
          :auto-upload="false"
          accept=".jpg,.jpeg,.png,.doc,.docx,.xls,.xlsx,.pdf,.txt"
          :file-list="fileList"
          :headers="uploadHeaders"
          :limit="7"
          :on-preview="onPreviewFile"
          :on-remove="onRemoveFileList"
          :before-remove="beforeRemoveFile"
          :on-success="fileSuccess"
          :on-exceed="onExceedFile"
          :on-change="onChangeFileList">
          <el-button :id="uploadBtnId" size="small" type="primary">上传附件</el-button>
        </el-upload>
        <el-table :data='viewFileList' style='width: 100%' v-show="authority !== 'add'">
<!--        <el-table :data='viewFileList' style='width: 100%' v-show="action">-->
          <el-table-column prop='name' label='附件名' :width="width"></el-table-column>
          <el-table-column prop="createBy" label='上传人' width='190px'></el-table-column>
          <el-table-column prop='createDate' label='上传日期' width='160px'></el-table-column>
          <el-table-column label='操作' header-align='center' width='120px' fixed='right'>
            <template slot-scope='scope'>
              <OperationIcon type='info' content='预览' placement='top-start' icon-name='el-icon-view'
                             @click='onViewFileList(scope.$index, scope.row)'></OperationIcon>
              <OperationIcon type='info' content='下载' placement='top-start' icon-name='el-icon-download'
                             @click='onUploadViewFile(scope.$index, scope.row)'></OperationIcon>
              <OperationIcon v-if="authority === 'edit'" type='info' content='删除' placement='top-start' icon-name='el-icon-close'
                             @click='onRemoveViewFile(scope.$index, scope.row)'></OperationIcon>
            </template>
          </el-table-column>
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>

</template>

<script>
  import { validatenull } from '@/utils/validate'
  import { fileUploadById, deleteSysFile, listSysFileAll } from '@/api/sys/sysFile'
  import { getPhotoById,getFiled } from "@/api/sys/sysSeting"
  import OperationIcon from '@/components/OperationIcon'
  import BaseUI from '@/views/components/baseUI'
  import { getLocalToken } from '@/utils/auth'

  let Base64 = require('js-base64').Base64
  export default {
    extends: BaseUI,
    name: "uploadFile",
    props: {
      value: {
        type: Array,
         default:function(){
            return {uploads:[]}
         }
      },
      action: {
        type: Boolean | String
      },
      objectId: {
        type: String | Number
      },
      title: {
        type: String,
        default: () => {
          return '附件上传'
        }
      }
    },
    data() {
      return {
        uploadBtnId: 'uploadBtn' + Math.random(),  // 上传文件动态id，解决一个页面多个文件上传组件存在问题

        activeNames: [], // 折叠面板
        uploadHeaders: {'X-Token': getLocalToken()},
        baseApi: process.env.BASE_API,
        fileList:  [],
        loading: false,
        viewFileList: [], // 附件回显列表
        fileName:"",
        uploadFiles:"",
      }
    },
    components: {
      OperationIcon
    },
    methods: {
      onUploadFile() { // 上传附件
        if (this.activeNames.indexOf('3') === -1) {
          this.activeNames.push("3")
        }
        console.log("anlksanlasl");
        document.getElementById(this.uploadBtnId).click()
      },
      onRemoveFileList: function (file, fileList) { // 移除文件
        this.value.uploads = fileList
      },
      onUploadViewFile(index, row) { // 下载
        // fileUploadById(row.id).then(responseData => {
        //   const content = responseData;
        //   this.download(content, row.name);
        // }).catch(error => {
        //   this.outputError(error)
        // });
         getFiled(row.id).then((res) => {
          console.log(res,'就是看');
         const src = `data:text/plain;base64,${res.base64Str}`;
        let name = res.name
      //  this.fileName=name
       // this.uploadFiles = src
        this.download(src,name)
        }).catch((error)=>{
            this.outputError(error)
        })
      },
      //下载方法
      download(uploadFiles, fileName) {
        // const blob = new Blob([content]);
        // const url = window.URL.createObjectURL(blob);
        // let dom = document.createElement("a");
        // dom.style.display = "none";
        // dom.href = url;
        // dom.setAttribute("download", fileName);
        // document.body.appendChild(dom);
        // dom.click();

        var blob = this.dataURLtoBlob(uploadFiles)
        const elink = document.createElement('a')
        // 设置下载文件名
        const timedate = Date.parse(new Date())
        elink.download = fileName
        elink.style.display = 'none'
        elink.href = URL.createObjectURL(blob)
        document.body.appendChild(elink)
        elink.click()
        document.body.removeChild(elink)
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
      onPreviewFile(file) {
        console.log(file);
      },
      onExceedFile(file, fileList) {
        this.$message.warning(`当前限制上传 7 个文件`);
      },
      beforeRemoveFile(file, fileList) {
        let index = this.fileList.indexOf(file);
        if (index > -1) {
          this.fileList.splice(index, 1);
          this.$emit("beforeRemove",file,fileList)
        }
      },
      fileSuccess(res, file){                 //上传文件成功
        console.log(file);
      },
      onChangeFileList(file, fileList) {
        this.fileList = fileList
       this.value.uploads = fileList
       this.$emit("getFileList",this.fileList)
      },
      onViewFileList(index, row) { // 预览
        let suffix = row.name.substring(row.name.lastIndexOf("."))
        let fileId = row.id + "" + suffix // 拼接参数，文件ID + 后缀名
        let token = getLocalToken()
        // 文件流访问路径
        let url = process.env.FILE_PREVIEW_URL + `sys/sysSeting/getFile/` + fileId + `?token=` + token
        window.open(process.env.KK_FILE_URL + encodeURIComponent(Base64.encode(url)));
      },
      onRemoveViewFile(index, row) {
        this.$confirm(`确定移除 ${ row.name }吗？`, '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(() => {
          this.viewFileList.splice(index, 1)
          //this.value.deletes.push(row.id)
          this.$emit("deleteFile",row.id)
        })
      },
      getFileList(){
        return this.fileList;
      },
      getViewFileList(){
        return this.viewFileList;
      },
      // 根据objectId，获取附件信息
      getSysFileAll() {
        this.setLoad()
        let file_search = {
          params: [{'columnName':'object_id', 'queryType': '=', 'value': this.objectId}]
        }
        listSysFileAll(file_search).then(responseData => {
          if(responseData.code == 100) {
            this.viewFileList = responseData.data
            if (!validatenull(this.viewFileList)) {
              this.activeNames.push("3")
            }
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      initOption() { // 初始化数据
        this.fileList = []  // 上传附件列表
        this.activeNames = [] // 折叠面板初始化
        this.viewFileList = []
        if (!validatenull(this.objectId)) {
          this.getSysFileAll()
        }
      }
    },
    computed: {
      authority() {
        if (validatenull(this.action)) {
          return "add";
        }
        if (typeof this.action === 'boolean') {
          if (this.action) {
            this.fileList = []  // 上传附件列表
            return "view";
          } else {
            return "add";
          }
        } else {
          if (this.action !== "add") {
            this.fileList = []  // 上传附件列表
          }
          return this.action;
        }
      }
    },
    watch: {
      'objectId': function(newVal, oldVal) {
        console.log(newVal, oldVal,"objectId变化了");
        if (!validatenull(this.objectId)) {
          this.getSysFileAll()
        }else{
          this.fileList = []  // 上传附件列表
          this.activeNames = [] // 折叠面板初始化
          this.viewFileList = []
        }
      },
      'action':function(newVal,oldVal){
        console.log('监听看');
        this.action=newVal
        if (newVal !== oldVal) {
          this.getSysFileAll()
        }
      }
    },
    mounted: function() {
      this.initOption()

    }
  }
</script>

<style lang="scss" scoped>
  /deep/ .upload-demo .el-upload {
    display: none!important;
  }
  .collapse-title {
    font-size: 14px;
    color: #606266;
  }
  .el-collapse-item__header {
    position: relative;
    border-bottom: 1px solid #EBEEF5!important;
    margin-bottom: 10px;
    .collapse-btn {
      // position: absolute;
      right: 30px;
    }
  }
</style>

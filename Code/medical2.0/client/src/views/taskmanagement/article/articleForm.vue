<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="65%"
    class="articleForm"
    @open="onDialogOpen()"
    :before-close="onDialogClose"
    v-loading="loading"
  >
    <div>
      <el-form
        ref="articlesForm"
        :model="articleForm"
        :rules="article"
        label-width="20%"
        class="demo-articleForm"
      >
        <el-form-item label="标题" prop="title">
          <el-input :disabled="dialogProps.audit || articleForm.auditstatus!='0'" placeholder="请输入" v-model="articleForm.title"></el-input>
        </el-form-item>
        <el-form-item label="摘要" prop="abstracts">
          <el-input
            type="textarea"
            :rows="3"
            :disabled="dialogProps.audit || articleForm.auditstatus!='0'"
            placeholder="请填写摘要"
            v-model="articleForm.abstracts"
          >
          </el-input>
        </el-form-item>

        <el-form-item v-if="originallinkcheckbox && articleForm.auditstatus=='0'" label="正文" prop="content">
      
            <editor
            ref="quilleditorref"
            v-model="articleForm.content"
            :min-height="192"
            :options="editoroption"
          >
          </editor>
        </el-form-item>
       
       <div v-if="!originallinkcheckbox || articleForm.auditstatus!='0'">
         <el-form-item   label="正文" prop="content">
           <div>
              <p v-html="articleForm.content"></p>
          </div>
        </el-form-item>
       </div>

       

        <el-form-item label="来源作者" prop="sources">
          <el-col :span="1">
            <el-checkbox :disabled="dialogProps.audit || articleForm.auditstatus!='0'" v-model="sourcecheckbox"></el-checkbox>
          </el-col>
          <el-col :span="23">
            <el-input
              v-model="articleForm.sources"
              :disabled="!sourcecheckbox || articleForm.auditstatus!='0'"
            ></el-input>
          </el-col>
        </el-form-item>
          
        <el-form-item label="原文链接" prop="originallink">
          <el-col :span="1">
            <el-checkbox :disabled="dialogProps.audit || articleForm.auditstatus!='0'" v-model="originallinkcheckbox"></el-checkbox>
          </el-col>
          <el-col :span="23">
            <el-input
              v-model="articleForm.originallink"
              :disabled="!originallinkcheckbox || articleForm.auditstatus!='0'"
            ></el-input>
          </el-col>
        </el-form-item>
        <!-- <el-form-item label="审核备注" prop="auditremarks" v-if="dialogProps.action == 'audit'">
            <el-input v-model="articleForm.auditremarks"></el-input>
        </el-form-item> -->
        <el-form-item v-if="dialogProps.action == 'audit'">
          <el-col :span="12">
            <el-button
              type="primary"
              :plain="true"
              @click="auditForm('1')"
              >审核通过</el-button
            >
            <el-button
              type="primary"
              :plain="true"
              @click="auditForm('2')"
              >不通过</el-button
            >
          </el-col>
        </el-form-item>
        
      </el-form>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button
        :disabled="flage"
        v-if="dialogProps.action == 'add'"
        type="primary"
        :plain="true"
        @click="submitForm('articlesForm')"
        >保 存</el-button
      >
      <el-button
        :disabled="flage"
        v-if="dialogProps.action == 'update'&&articleForm.auditstatus=='0'"
        type="primary"
        :plain="true"
        @click="updateForm('articlesForm')"
        >修 改</el-button
      >

      <el-button :plain="true" type="primary" @click="onDialogClose()"
        >取 消</el-button
      >
    </span>
  </el-dialog>
</template>


<script>
import BaseUI from "@/views/components/baseUI";
import OperationIcon from "@/components/OperationIcon";
import {
  editSave,
  getarticleid,
  editUpdate,
  editAudit,
} from "@/api/taskmanagement/article";
import Editor from "./ue";
export default {
  extends: BaseUI,
  name: "article-form",
  components: {
    OperationIcon,
    Editor,
  },
  computed: {
    key() {
      return this.$route.name != undefined
        ? this.$route.name + +new Date()
        : this.$route + +new Date();
    },
  },
  props: ["closeValue"],
  data() {
    var validatePass = (rule, value, callback) => {
      console.log(this.articleForm.content);
      if (value === "") {
        callback(new Error("请输入正文"));
      } else {
        if (this.articleForm.content !== "") {
          this.$refs.articleForm.validateField("content");
        }
        callback();
      }
    };
    return {
      dialogProps: {
        visible: false,
        action: "",
        title: "",
        audit: false,
      },
      editorflg: true,
      sourcecheckbox: false,
      originallinkcheckbox: false,
      articleForm: {
        companyid: currentUser.company.id, //诊所标识
        userid: currentUser.id, //发布人标识
        username: currentUser.name, //发布人名称
        aricledate: "", //发布时间
        title: "", //标题
        abstracts: "", //摘要
        content: "", //内容
        sources: "", //来源作者
        originallink: "", //原文链接
        auditstatus: "0", //默认待审核
        id: "",
      },

      article: {
        title: [{ required: true, message: "请输入标题", trigger: "blur" }],
        abstracts: [
          { required: true, message: "请输入摘要", trigger: "blur" },
          // { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: "请输入正文内容", trigger: "change" },
        ],
      },
    };
  },
  methods: {
    tableRowClassName({ row, rowIndex }) {
      if (rowIndex === 1) {
        return "warning-row";
      } else if (rowIndex === 3) {
        return "success-row";
      }
      return "";
    },
    emptylist() {
      this.articleForm = {
        companyid: currentUser.company.id, //诊所标识
        userid: currentUser.id, //发布人标识
        username: currentUser.name, //发布人名称
        aricledate: "", //发布时间
        title: "", //标题
        abstracts: "", //摘要
        content: "", //内容
        sources: "", //来源作者
        originallink: "", //原文链接
        auditstatus: "0", //默认待审核
      };
    },
    onDialogClose() {
      this.originallinkcheckbox = false
      this.sourcecheckbox = false;
      this.originallinkcheckbox = false;
      this.editorflg = false;
      this.emptylist();
      this.dialogProps.visible = false;
      this.$emit("typeclick", "");
      
    },
    onDialogOpen() {
      this.editorflg = true;
      this.$nextTick(() => {
        this.$refs["articlesForm"].clearValidate();
      });
    },
    submitForm(formName) {
      //debugger
      console.log(this.$refs[formName]);
      this.$refs[formName].validate((valid) => {
        if (valid) {
          //   console.info("时间" + this.ruleForm.taskdeadline);
          //   this.ruleForm.taskinitiator = currentUser.id;
          editSave(this.articleForm).then((responseData) => {
            if (responseData.code == 100) {
              this.onDialogClose();
              console.info("添加成功");
            }
          });
        }
      });
    },
    updateForm(formName) {
      //debugger
      console.log(this.$refs[formName]);
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (
            this.articleForm.auditstatus != "0" &&
            this.articleForm.auditstatus != "2"
          ) {
            this.showMessage("已审核不能进行修改");
          } else {
            if (this.articleForm.userid == currentUser.id) {
              this.articleForm.auditstatus = "0"; //修改审核状态重新调整为0
              editUpdate(this.articleForm).then((responseData) => {
                if (responseData.code == 100) {
                  this.onDialogClose();
                  console.info("添加成功");
                }
              });
            } else {
              this.showMessage("非本人发布文章不能进行修改");
            }
          }
        }
      });
    },
    auditForm(type) {
      console.log(type);
      this.articleForm.auditstatus = type; //修改审核状态
      editAudit(this.articleForm).then((responseData) => {
        if (responseData.code == 100) {
          this.onDialogClose();
          console.info("成功");
        }
      });
          
    },
  },
  mounted: function () {
    this.$nextTick(() => {
      this.$on("openAddworkbenchDialog", function (viewlist) {
        this.emptylist();
        console.log("弹框打开", viewlist);
        let titles = viewlist.view;
        // if (viewlist.view == "") {
        //   titles = "新增";
        // } else if (viewlist.view != "") {
        //   titles = "修改";
        // } else {
        //   titles = "";
        // }

        if (titles == "修改" || titles == "审核" || titles == "查看") {
          this.articleForm.id = viewlist.id;
          getarticleid(this.articleForm).then((responseData) => {
            console.log("查询出参", responseData);
            if (responseData.code == 100) {
              let refdata = responseData.data;
              this.articleForm.aricledate = refdata.aricledate;
              this.articleForm.title = refdata.title;
              this.articleForm.abstracts = refdata.abstracts;
              this.articleForm.content = refdata.content;
              this.articleForm.sources = refdata.sources;
              this.articleForm.originallink = refdata.originallink;
              this.articleForm.userid = refdata.userid;
              this.articleForm.auditstatus = refdata.auditstatus;
            }
          });
          this.dialogProps.action = "update";
          this.sourcecheckbox = true;
          this.originallinkcheckbox = true;
          if (titles == "审核") {
            this.dialogProps.action = "audit";
            this.dialogProps.audit = true;
            this.sourcecheckbox = false;
            this.originallinkcheckbox = false;
          }else if(titles == "查看"){
          console.log("jinlai ");
            this.dialogProps.action = "view";
            this.dialogProps.audit = true;
            this.sourcecheckbox = false;
            this.originallinkcheckbox = false;
        }
        } else {
          this.dialogProps.action = "add";

          this.dialogProps.audit = false;
          this.sourcecheckbox = true;
          this.originallinkcheckbox = true;
        }

        this.dialogProps.title = titles;
        this.tabIndex = "1";
        this.dialogProps.visible = true;
        this.province = "";
        this.city = "";
        this.area = "";
      });
    });
  },
};
</script>


<style>
.el-table .warning-row {
  background: oldlace;
  color: #3eec5b;
  color: #a7761a;
  color: #f5314b;
}

.el-table .success-row {
  background: #f0f9eb;
}
</style>

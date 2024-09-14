<template>
  <el-dialog
    :title="dialogProps.title"
    :visible.sync="dialogProps.visible"
    :close-on-click-modal="false"
    width="60%"
    @open="onDialogOpen()"
    v-loading="loading"
  >
    <div>
      <el-row>
        <el-form :label-position="labelPosition" :model="formLabelAlign"
                 :rules="rules"
                 ref="formLabelAlign">
          <el-col :span="24">
            <el-form-item>
              <span class="bt">模板基本信息</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              prop="mbmc"
              label="模板名称"
            >
              <el-input v-model="formLabelAlign.mbmc" style="width:70%"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item
              label="模板类型"
            >
              <el-radio v-model="mblx" label="0">通用</el-radio>
              <el-radio v-model="mblx" label="1">个人</el-radio>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item
              label="病历类型"
            >
              <el-radio v-model="bllx" label="0">西医病历</el-radio>
              <el-radio v-model="bllx" label="1">中医病历</el-radio>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item>
              <span class="bt">模板内容</span>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="主诉">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%"
                v-model="formLabelAlign.zs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个人史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%"
                v-model="formLabelAlign.grs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="过敏史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%"
                v-model="formLabelAlign.gms">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="疾病史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.jbs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="传染病史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.crbs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="手术史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.sss">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="输血史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.sxs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="体格检查">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.tgjc">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="急诊诊断">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.jzzd">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="急诊效果">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.jzxg">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="家族史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.jzs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="月经史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.yjz">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="婚育史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.hys">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="辅助检查">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.fzjc">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="现病史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.xbs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="既往史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.jws">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="流行病学史">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.lxbxs">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="其他检查">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.qtjc">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理情况">
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.clqk">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="个体化健康教育" >
              <el-input
                type="textarea"
                autosize
                placeholder="请输入内容"
                style="width:50%;"
                v-model="formLabelAlign.gthjkjy">
              </el-input>
            </el-form-item>
          </el-col>
        </el-form>
      </el-row>
      <!-- <div class="footer">
          <el-button type="primary" @click="submitForm('formLabelAlign')" >提交</el-button>
          <el-button @click="resetForm('formLabelAlign')" >重置</el-button>
      </div> -->
    </div>
    <span slot='footer' class='dialog-footer'>
      <el-button :disabled="flage" type='primary' :plain='true' @click="submitForm('formLabelAlign')">保 存</el-button>
      <el-button :plain='true' type="primary" @click='onDialogClose()'>取 消</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import BaseUI from "@/views/components/baseUI";
  import OperationIcon from "@/components/OperationIcon";
  import VDistpicker from 'v-distpicker';
  import {saveblmb, selectmbbm, updatembbm} from '@/api/outpatient/blmb'


  export default {
    extends: BaseUI,
    name: "blmb-form",
    components: {
      OperationIcon,
      VDistpicker
    },
    props: ['closeValue'],
    data() {
      return {
        mblx: "0",
        bllx: "0",
        labelPosition: 'top',
        formLabelAlign: {
          mbbm: '',
          companyId: currentUser.company.id,
          delFlag: '0',
          createdBy: currentUser.name + "(" + currentUser.loginname + ")",
          createdTime: '',
          updatedBy: '',
          updatedTime: '',
          createdID: currentUser.id,
          mbmc: '',
          mblx: '',
          bllx: '',
          zs: '',
          grs: '',
          gms: '',
          jbs: '',
          crbs: '',
          sss: '',
          sxs: '',
          tgjc: '',
          jzzd: '',
          jzxg: '',
          jzs: '',
          yjz: '',
          hys: '',
          fzjc: '',
          xbs: '',
          jws: '',
          lxbxs: '',
          qtjc: '',
          clqk: '',
          gthjkjy: '',
        },
        dialogProps: {
          visible: false,
          action: '',
          title: '',
        },
        rules: {
          mbmc: [
            {required: true, message: '请输入模板名称', trigger: 'blur'},
          ],

        },
        blmbcxrc: {
          companyId: currentUser.company.id,
          mbbm: '',
        },
      };
    },
    methods: {
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.formLabelAlign.mblx = this.mblx;
            this.formLabelAlign.bllx = this.bllx;
            if (this.formLabelAlign.mbbm != '' && this.formLabelAlign.mbbm != null) {
              this.formLabelAlign.updatedBy = currentUser.name + "(" + currentUser.loginname + ")",
                this.Updateblmbinfo(this.formLabelAlign)
            } else {
              saveblmb(this.formLabelAlign).then((responseData) => {
                if (responseData.code == 100) {
                  this.onDialogClose();
                  this.$message.success(responseData.msg);
                }
              })
                .catch((error) => {
                  this.$message.error(error);
                });
            }
          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      resetForm(formName) {
        this.$refs[formName].resetFields();
      },
      onDialogOpen() {
        this.$nextTick(() => {
          this.$refs["blmbForm"].clearValidate();
        });
      },
      onDialogClose() {
        this.$emit('typeclick', "");
        this.dialogProps.visible = false;

      },
      Getselectmbbm(types) {
        this.blmbcxrc.mbbm = types.mbbm;
        selectmbbm(this.blmbcxrc).then((responseData) => {
          if (responseData.code == 100) {
            responseData.data.forEach(values => {
              this.bllx = values.bllx;
              this.mblx = values.mblx;
              this.formLabelAlign.mbbm = values.mbbm;
              this.formLabelAligncompanyId = currentUser.company.id;
              this.formLabelAlign.delFlag = values.delFlag;
              this.formLabelAlign.createdBy = values.createdBy;
              this.formLabelAlign.createdTime = values.createdTime;
              this.formLabelAlign.updatedBy = currentUser.name + "(" + currentUser.loginname + ")";
              this.formLabelAlign.updatedTime = '';
              this.formLabelAlign.createdID = values.createdID;
              this.formLabelAlign.mbmc = values.mbmc;
              this.formLabelAlign.mblx = values.mblx;
              this.formLabelAlign.bllx = values.bllx;
              this.formLabelAlign.zs = values.zs;
              this.formLabelAlign.grs = values.grs;
              this.formLabelAlign.gms = values.gms;
              this.formLabelAlign.jbs = values.jbs;
              this.formLabelAlign.crbs = values.crbs;
              this.formLabelAlign.sss = values.sss;
              this.formLabelAlign.sxs = values.sxs;
              this.formLabelAlign.tgjc = values.tgjc;
              this.formLabelAlign.jzzd = values.jzzd;
              this.formLabelAlign.jzxg = values.jzxg;
              this.formLabelAlign.jzs = values.jzs;
              this.formLabelAlign.yjz = values.yjz;
              this.formLabelAlign.hys = values.hys;
              this.formLabelAlign.fzjc = values.fzjc;
              this.formLabelAlign.xbs = values.xbs;
              this.formLabelAlign.jws = values.jws;
              this.formLabelAlign.lxbxs = values.lxbxs;
              this.formLabelAlign.qtjc = values.qtjc;
              this.formLabelAlign.clqk = values.clqk;
              this.formLabelAlign.gthjkjy = values.gthjkjy;
            });
          }
        })
          .catch((error) => {
            this.$message.error(error);
          });
      },
      Updateblmbinfo(values) {
        updatembbm(values).then((responseData) => {
          if (responseData.code == 100) {
            this.onDialogClose();
            this.$message.success(responseData.msg);
          }
        })
          .catch((error) => {
            this.$message.error(error);
          });
      },
    },
    mounted: function () {
      this.$nextTick(() => {
        this.$on('openAddworkbenchDialog', function (types) {
          let titles = "";
          if (types == "") {
            titles = "新增"
          } else if (types != "") {
            titles = "修改"
          } else {
            titles = ""
          }
          ;
          if (types != "" && types != null) {
            this.Getselectmbbm(types);
          } else {
            this.mblx = "0",
              this.bllx = "0",
              this.formLabelAlign = {
                mbbm: '',
                companyId: currentUser.company.id,
                delFlag: '0',
                createdBy: currentUser.name + "(" + currentUser.loginname + ")",
                createdTime: '',
                updatedBy: '',
                updatedTime: '',
                createdID: currentUser.id,
                mbmc: '',
                mblx: '',
                bllx: '',
                zs: '',
                grs: '',
                gms: '',
                jbs: '',
                crbs: '',
                sss: '',
                sxs: '',
                tgjc: '',
                jzzd: '',
                jzxg: '',
                jzs: '',
                yjz: '',
                hys: '',
                fzjc: '',
                xbs: '',
                jws: '',
                lxbxs: '',
                qtjc: '',
                clqk: '',
                gthjkjy: '',
              };
          }
          this.dialogProps.action = 'add'
          this.dialogProps.title = titles + '模板信息'
          this.tabIndex = '1'
          this.dialogProps.visible = true
          this.province = ''
          this.city = ''
          this.area = ''
        })
      });
    }
  }
</script>

<style lang="scss">
  .bt {
    font-size: 18px;
    font-weight: bold;
  }

  .footer {
    position: fixed;
    bottom: 0;
    width: 80%;
    line-height: var(--footer-height);
    text-align: right;
    margin: 10px 0 10px 0;
  }
</style>

<template>
  <el-row v-loading='loading'>
    <!-- 历史记录  -->
    <History :bussObject='curentRow'></History>
    <!-- 编辑窗口  -->
    <patient-form ref='patientForm' :permission='permission' v-on:save-finished='getPatientList()'></patient-form>
    <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-row v-if='!moreCodition' class='search-row'>
          <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm'
                   :inline-message='true'>
            <el-col :span="4">
              <el-form-item label='患者姓名' prop='name'>
                <el-input v-model='queryModel.name' :clearable='true' placeholder='请输入患者姓名'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="5">
              <el-form-item label='身份证号' prop='card'>
                <el-input v-model='queryModel.card' :clearable='true' placeholder='请输入身份证号'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='科室' prop='department'>
                <el-input v-model='queryModel.department' :clearable='true' placeholder='请输入科室'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='医生' prop='medic'>
                <el-input v-model='queryModel.medic' :clearable='true' placeholder='请输入医生姓名'></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='状态' prop='status'>
                <el-select v-model='queryModel.status' :clearable='true' placeholder='请选择状态' clearable>
                  <el-option
                    v-for="item in options"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <!--
                <el-input v-model='queryModel.status' :clearable='true' placeholder='请选择状态'></el-input>
                -->
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="就诊时间" prop="diagnosisTime">
                <el-date-picker
                  :picker-options="filerData.pickerOptions"
                  v-model="queryModel.diagnosisTime"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  :default-time="['00:00:00', '23:59:59']"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
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
            <el-col :span="3" style="text-align:right;padding-right:5px">
              <el-button-group>
                <el-button
                  v-show="permission.add"
                  type="primary"
                  icon="el-icon-plus"
                  @click="onCreatePatient()"
                >添加
                </el-button
                >
              </el-button-group>
            </el-col>
          </el-form>
        </el-row>
        <QueryForm v-else v-model='moreParm' :tableId='tableId' :schemeId='schemeId' :routerId='$route.meta.routerId'
                   @search='onSearch()' @moreCodition='onMoreCodition()'></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->
      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <div @mouseleave='moveTableOutside'>
            <el-table ref="patientTableRef" :data='patientList' border @sort-change='onSortChange'
                      @header-dragend='onChangeWidth' :cell-class-name='cellClassName'
                      :header-cell-class-name='headerCellClassName' highlight-current-row>
              <el-table-column
                label="序号"
                width="70px"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column prop="name" label="姓名"></el-table-column>
              <el-table-column prop="gender" label="性别"></el-table-column>
              <el-table-column prop="card" label="证件号"></el-table-column>
              <el-table-column prop="department" label="申请科室"></el-table-column>
              <el-table-column prop="medic" label="申请医生"></el-table-column>
              <el-table-column prop="diagnosisTime" label="诊疗时间"></el-table-column>
              <el-table-column prop="createDate" label="创建时间"></el-table-column>
              <el-table-column prop="status" label="状态">
                <template slot-scope='scope'>
                  <span v-if="scope.row.status === '0'">未提交</span>
                  <span v-if="scope.row.status === '1'">待确认</span>
                  <span v-if="scope.row.status === '2'">就诊中</span>
                  <span v-if="scope.row.status === '3'">已结束</span>
                  <span v-if="scope.row.status === '4'">已发药</span>
                  <span v-if="scope.row.status === '5'">已退回</span>
                </template>
              </el-table-column>

              <!--表行级操作按钮-->
              <el-table-column label='操作' fixed="right" header-align='center' :key="Math.random()" :width='150'>
                <template slot-scope='scope'>
                  <span v-if="scope.row.status === '0'" style="color: #0a7be0"
                        @click="submit(scope.$index, scope.row)">提交&nbsp;</span>
                  <span v-if="scope.row.status === '1'" style="color: #0a7be0"
                        @click="cancel(scope.$index, scope.row)">撤销&nbsp;</span>
                  <span v-if="scope.row.status === '2'" style="color: #0a7be0"
                        @click="conference(scope.$index, scope.row)">加入会议&nbsp;</span>
                <!--  <span v-if="scope.row.status === '2'" style="color: #0a7be0"
                        @click="sponsorConference(scope.$index, scope.row)">发起会议&nbsp;</span>-->
                  <span style="color: #0a7be0" @click="viewDiagnosis(scope.$index, scope.row)">详情&nbsp;</span>
                  <!--
                  <span style="color: #0a7be0" @click="statusEdit(scope.$index, scope.row)">状态&nbsp;</span>
                  -->
                  <span v-if="scope.row.status === '0'" style="color: red"
                        @click="onDeleteDiagnosis(scope.$index, scope.row)">删除&nbsp;</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏  开始 -->
      <el-row>
        <el-col :span='24'>
          <el-pagination
            background
            @size-change='onSizeChange'
            @current-change='onCurrentChange'
            :current-page.sync='currentPage'
            :page-sizes='[ 20, 50, 100, patientTotal]'
            :page-size='20'
            layout='total, sizes, prev, pager, next, jumper'
            :total='patientTotal'>
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->     </el-card>
  </el-row>
</template>

<script>
  import {validatenull} from '@/utils/validate'
  // import {listPatientPage, getPatientById, deletePatient} from '@/api/outpatient/patient'
  import {listDiagnosisPage, getDiagnosisById, saveDiagnosis, deleteDiagnosis } from '@/api/outpatient/remoteDiagnosisTreatment'
  import {listResourcePermission} from '@/api/admin/common/permission'
  import PatientForm from './patientForm'
  import ExportExcelButton from '@/components/ExportExcelButton'
  import ViewColumnsSelect from '@/views/components/ViewColumnsSelect'
  import QueryForm from '@/views/components/queryForm'
  import MainUI from '@/views/components/mainUI'
  import OperationIcon from '@/components/OperationIcon'
  import History from '@/views/components/history'
  import {getLocalToken} from '@/utils/auth';
  import axios from 'axios'
  import Vue from "vue";

  Vue.prototype.$axios = axios;

  const loginForm = {Data: "API.Manage", AppId: "Oh_Newtouch_Clinic"}
  const config = {
    headers: {
      Token: getLocalToken()
    }
  }
  export default {
    extends: MainUI,
    components: {
      PatientForm,
      ExportExcelButton,
      ViewColumnsSelect,
      QueryForm,
      OperationIcon,
      History
    },
    data() {
      return {
        tokenData: null,
        hisPatient: null,
        conferenceReturn: {}, // 会议申请返回内容
        permission: {
          caseDialogVisible: false,
          view: false,
          add: false,
          edit: false,
          remove: false,
          export: false
        },
        filerData: {
          pickerOptions: {
          },
        },
        options: [{
          value: '0',
          label: '未提交'
        }, {
          value: '1',
          label: '待确认'
        }, {
          value: '2',
          label: '就诊中'
        }, {
          value: '3',
          label: '已结束'
        }, {
          value: '4',
          label: '已发药'
        }, {
          value: '5',
          label: '已退回'
        }],
        queryTypes: {
          'name': 'like',
          'phone': 'like',
          'card': '=',
        },
        queryModel: {
          'name': '',   // 患者姓名
          'phone': '',   // 联系方式
          'card': '',   // 身份证号
          'department': '',   // 科室
          'medic': '',   // 医生
          'diagnosisTime': [this.addCreateDate(), this.$moment(new Date()).endOf("day")],//时间范围
          'createDate': [this.addCreateDate(), this.$moment(new Date()).endOf("day")],//时间范围

        },
        search: {
          params: [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}],
          offset: 0,
          limit: 20,
          columnName: '',      // 排序字段名
          order: ''            // 排序
        },
        currentPage: 1,
        patientTotal: 0,
        patientList: [],
        adetailsList: {},


        oprColumnWidth: 140,  // 操作列宽
        tableId: '1008489176147648530',
        schemeId: '1008489176147648553'
      }
    },
    methods: {
      reset() {
        this.$refs.queryForm.resetFields()
        this.onSearch()
      },
      getPatientList() {
        this.setLoad()
        /* 查询参数 和数据权限 */
        this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
        if (this.moreCodition) {
          this.search.params = this.search.params.concat(this.compositeCondition())
        } else {
          // 查询参数: 患者姓名
          this.search.params.push({
            columnName: 'name',
            queryType: 'like',
            value: this.queryModel.name
          })
          // 查询参数: 状态
          this.search.params.push({
            columnName: 'status',
            queryType: 'like',
            value: this.queryModel.status
          })
          // 查询参数: 身份证号
          this.search.params.push({
            columnName: 'card',
            queryType: '=',
            value: this.queryModel.card
          })
          this.search.params.push({
            columnName: 'department',
            queryType: '=',
            value: this.queryModel.department
          })
          this.search.params.push({
            columnName: 'medic',
            queryType: '=',
            value: this.queryModel.medic
          })
          this.search.params.push({
            columnName: "diagnosis_time",
            queryType: ">=",
            value: this.queryModel.diagnosisTime
              ? this.$moment(this.queryModel.diagnosisTime[0]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          this.search.params.push({
            columnName: "diagnosis_time",
            queryType: "<=",
            value: this.queryModel.diagnosisTime
              ? this.$moment(this.queryModel.diagnosisTime[1]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: ">=",
            value: this.queryModel.createDate
              ? this.$moment(this.queryModel.createDate[0]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
          this.search.params.push({
            columnName: "create_date",
            queryType: "<=",
            value: this.queryModel.createDate
              ? this.$moment(this.queryModel.createDate[1]).format(
                "YYYY-MM-DD HH:mm:ss"
              )
              : "",
          });
        }
        // 数据权限: 患者表patient
        this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
        listDiagnosisPage(this.search).then(responseData => {
          if (responseData.code == 100) {
            this.patientTotal = responseData.data.total
            this.patientList = responseData.data.rows
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      indexMethod(index) {
        return (this.currentPage - 1) * this.search.limit + index + 1;
      },
      addCreateDate() {

        let myDate = new Date();
        let lw = new Date(myDate.getTime() - 1000 * 60 * 60 * 24 * 30); //最后一个数字30可改，30天的意思
        console.log(lw.getDate());
        let lastY = lw.getFullYear();
        let lastM = lw.getMonth() + 1;
        let lastD = lw.getDate();
        let housrs = lw.getHours();
        let minutes = lw.getMinutes()
        let seconds = lw.getSeconds()
        let startData =
          lastY +
          "-" +
          (lastM < 10 ? "0" + lastM : lastM) +
          "-" +
          (lastD < 10 ? "0" + lastD : lastD) +
          " " + housrs + ":" + minutes + ":" + seconds //三十天之前日期
        return startData;

      },
      onSearch() {
        if (this.moreCodition) {
          this.search.offset = 0
          this.currentPage = 1
          this.getPatientList()
        } else {
          this.$refs['queryForm'].validate(valid => {
            if (valid) {
              this.search.offset = 0
              this.currentPage = 1
              this.getPatientList()
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
        this.getPatientList()
      },
      onCurrentChange(val) {
        this.search.offset = (val - 1) * this.search.limit
        this.currentPage = val
        this.getPatientList()
      },
      async pageInit() {
        this.setLoad()
        try {
          this.initOptions(this.queryModel)
          this.search.params = [{columnName: 'company_id', queryType: '=', value: currentUser.company.id}]
          // 数据权限: 患者表patient
          this.pushDataPermissions(this.search.params, this.$route.meta.routerId, this.tableId)
          let [listPatientRespData, listPermissionRespData] = await Promise.all([
            listDiagnosisPage(this.search),
            listResourcePermission(this.$route.meta.routerId)
          ])
          if (listPatientRespData.code == 100 && listPermissionRespData.code == 100) {
            this.patientTotal = listPatientRespData.data.total
            this.patientList = listPatientRespData.data.rows
            for (let i = 0; i < this.patientList.length; i++) {
              console.log(this.patientList[i].diagnosisTime)
              if (this.patientList[i].diagnosisTime) {
                this.patientList[i].diagnosisTime = this.patientList[i].diagnosisTime.replace("T", " ")
              }
            }
            this.permission.view = listPermissionRespData.data.find(item => {
              return item.permission === 'patient:read'
            })
            this.permission.export = listPermissionRespData.data.find(item => {
              return item.permission === 'patient:export'
            })
            this.permission.add = listPermissionRespData.data.find(item => {
              return item.permission === 'patient:create'
            })
            this.permission.edit = listPermissionRespData.data.find(item => {
              return item.permission === 'patient:update'
            })
            this.permission.remove = listPermissionRespData.data.find(item => {
              return item.permission === 'patient:delete'
            })
          } else {
            this.showMessage(listPermissionRespData.code != 100 ? listPermissionRespData : listPatientRespData)
          }
          this.resetLoad()
        } catch (error) {
          this.outputError(error)
        }
      },
      // 提交保存申请
      submit(index, row) {
        this.setLoad()
        getDiagnosisById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.patientForm.$emit('openViewPatientDialog', responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      // 查看详情信息
      viewDiagnosis(index, row) {
        this.setLoad()
        getDiagnosisById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.patientForm.$emit('openCopyPatientDialog', responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      // 获取远程诊所token
      async getToken() {
        await this.$axios.post('/token/Auth/GetAppFrienAuthToken', loginForm, config)
          .then((response) => {
            this.tokenData = response.data.BusData.data.Token
            console.log(response.data.BusData.data.Token)
          })
          .catch(function (error) {
            console.log(error);
          });
      },
      // 重新申请发起会议
      async sponsorConference(index, row){
        await this.getToken()
        await this.$axios.post('apis/RemoteTreated/DoctorMeetingApply', {
          Data: {
            usercode: row.medicId,
            username: row.medic,
            applyId: row.id,
            roomReset: true,
            roomid: row.conferenceId
          },
          OrganizeId: row.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            if (response.data.BusData.code === 20000) {
              this.$message.warning("诊疗申请信息异常，请重新发送申请！")
            }else if (response.data.BusData.code === 10000){

            }
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 加入会议
      async conference(index, row) {
        await this.getToken();
        await this.$axios.post('apis/RemoteTreated/UserJoinMeetingApply', {
          Data: {
            usercode: currentUser.id,
            username: currentUser.name,
            applyId: row.id,
            roomid: row.conferenceId
          },
          OrganizeId: row.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            if (response.data.BusData.code === 40004) {
              this.$message.warning("机构信息及关键申请信息不可为空！")
            } else if(response.data.BusData.code === 20000){
              this.$message.warning("音视频会议接口返回异常：会议房间已过期，请使用其他房间号！")

            } else if (response.data.BusData.code === 10000) {
              this.conferenceReturn = response.data.BusData
              // window.open('https://61.172.179.30:17015/' + this.conferenceReturn.data.roompath,'_blank')
              var i = {
                "rtcuserid": this.conferenceReturn.data.rtcuserid,
                  "roomid": this.conferenceReturn.data.roomid,
                  "username": this.conferenceReturn.data.username,
                  "roompath": this.conferenceReturn.data.roompath,
                  "organization": this.conferenceReturn.data.organization,
                  "device": this.conferenceReturn.data.device
              }
              // var a = 'https://61.172.179.30:17015/' + this.conferenceReturn.data.roompath
              // console.log(i,a)
              var child = window.open('https://61.172.179.30:17015/' + this.conferenceReturn.data.roompath);
              setTimeout(function () {
                child.postMessage(i, "*");
              }, 3000);

              this.$message.success("会议加入成功！")
              /*setTimeout(() =>{
                window.postMessage(i,a,i)
              },2000)*/

            }
          })
          .catch(function (error) {
            console.log(error);
          });
      },

      // 撤销申请
      cancel(index, row){
        this.$confirm('确定撤销申请吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          row.status = "0"
          this.statusEdit(index, row)
        }).catch(() => {})
      },

      // 状态修改
      async statusEdit(index, row){
        await this.getToken();
        await this.$axios.post('apis/RemoteTreated/TreatedApplyInfo', {
          Data: {
            sqlsh: row.id // 申请流水号
          },
          OrganizeId: row.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        })
          .then((response) => {
            this.hisPatient = response.data.BusData.data
            console.log("his患者会诊信息"+JSON.stringify(this.hisPatient))
            console.log(response);
          })
          .catch(function (error) {
            console.log(error);
          });
        console.log("row.id" + row.id)
        await this.$axios.post('apis/RemoteTreated/TreatedApplyCancel', {
          Data: {
            ApplyId: this.hisPatient.Id
          },
          OrganizeId: row.hospitalId,
          AppId: "Oh_Newtouch_Clinic",
          Timestamp: new Date()
        }, {
          headers: {
            Authorization: "Bearer " + this.tokenData
          }
        }).then((response) => {
          if (response.data.BusData.code === 10000){
            row.diagnosisTime = null;
            saveDiagnosis(row).then(responseData => {
              if (responseData.code == 100) {
                this.reset()
              } else {
                this.reset()
                this.showMessage(responseData)
              }
              this.resetLoad()
            }).catch(error => {
              this.outputError(error)
            })
          } else {
            this.$message.warning("诊疗申请信息异常，请联系管理员后重新发送申请!")
          }
          console.log(response);
        })
          .catch((error) => {
            console.log(error);
          });
      },
      onCreatePatient() {
        this.$refs.patientForm.$emit('openAddPatientDialog')
      },
      onEditPatient(index, row) {
        this.setLoad()
        getDiagnosisById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.patientForm.$emit('openEditPatientDialog', responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onCopyPatient(index, row) {
        this.setLoad()
        getDiagnosisById(row.id).then(responseData => {
          if (responseData.code == 100) {
            this.$refs.patientForm.$emit('openCopyPatientDialog', responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      },
      onDeleteDiagnosis(index, row) {
        this.$confirm('确定删除吗？', '确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.setLoad()
          console.log("row.id"+row.id)
          row.diagnosisTime = null;
          deleteDiagnosis(row).then(responseData => {
            if (responseData.code === 100) {
              this.getPatientList()
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
        this.reset();
      },
      onSortChange(orderby) {
        if (validatenull(orderby.prop)) {
          this.search.columnName = ''
          this.search.order = ''
        } else {
          this.search.columnName = orderby.prop
          this.search.order = orderby.order === 'descending' ? 'desc' : 'asc'
        }

        this.getPatientList()
      },
      initOptions(This) {
      }
    },
    watch: {
      // tableData是el-table绑定的数据
      tableData: {
        // 解决表格显示错位问题
        handler() {
          this.$nextTick(() => {
            // tableRef是el-table绑定的ref属性值
            this.$refs.patientTableRef.doLayout()// 对 Table 进行重新布局
          })
        },
        deep: true
      }
    },
    updated() {
      //  if(this.$refs.patientTableRef){
      this.$nextTick(() => {
        // tableRef是el-table绑定的ref属性值
        this.$refs.patientTableRef.doLayout()// 对 Table 进行重新布局
      })
      //}
    },
    mounted() {
      this.pageInit()
    }
  }
</script>
<style lang="scss" scoped>
  .page-container {
    padding: 0;
  }

  ;

  /deep/ .el-table {
    .el-table__fixed-body-wrapper {
      top: 47px !important;
    }
  }

  ;

  /deep/ .el-table__fixed-right-patch {
    width: 5px !important
  }

  ;

  /deep/ .el-table colgroup col[name='gutter'] {
    width: 5px !important
  }

  ;

  /deep/ .el-table__body {
    width: 100% !important
  }

  ;

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
<style scoped>
  /deep/ .el-table__body-wrapper {
    height: calc(100% - 44px) !important;
  }
</style>

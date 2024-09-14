<template>
  <el-card class="page-container" style="padding: 0px;">
    <el-row>
      <el-col :span="24">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="就诊时间" :span="8" style="margin-right: 3px;">
            <div class="block" style="width:50%">
              <el-date-picker
                v-model="TimeInterval"
                type="datetimerange"
                value-format="yyyy-MM-dd HH:mm:ss"
                :picker-options="pickerOptions"
                :default-time="['00:00:00','23:59:59']"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                align="right"
                :clearable="false">
              </el-date-picker>
            </div>
          </el-form-item>
          <el-form-item label="名称" style="margin-right: 3px;">
            <el-input v-model="formInline.patientName" placeholder="请输入患者名称"></el-input>
          </el-form-item>
          <el-form-item label="身份证号" style="margin-right: 3px;">
            <el-input v-model="formInline.certificate" placeholder="请输入患者身份证号"></el-input>
          </el-form-item>
          <el-form-item style="margin-right: 3px;">
            <el-button type="primary" @click="onSubmit">查询</el-button>
            <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
            <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->
            <el-button type="primary" icon="el-icon-download" @click='onExport' :plain='true' >导出</el-button>
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="24">
        <el-table
          :data="tableData"
          ref="tableData"
          border
          height="calc(100vh - 280px)"
        >
          <el-table-column
            label="序号"
            type="index"
            :index="indexMethod"
            align="center">
          </el-table-column>

          <el-table-column align="center" prop="patientName" label="患者姓名"></el-table-column>
          <el-table-column align="center" prop="patriarchName" label="家长姓名"></el-table-column>
          <el-table-column align="center" prop="sex" label="性别" :formatter="stateFormat"></el-table-column>
          <el-table-column align="center" prop="age" label="患者年龄"></el-table-column>
          <el-table-column align="center" prop="nation" label="民族"></el-table-column>
          <el-table-column align="center" prop="occupation" label="职业"></el-table-column>
          <el-table-column align="center" prop="address" label="详细地址"></el-table-column>
          <el-table-column align="center" prop="visitDate" label="就诊日期"></el-table-column>
          <el-table-column align="center" prop="initialVisit" label="初/复诊">
            <template slot-scope="scope">
              <span>{{ scope.row.initialVisit === "registrationSource_0" ? '初诊' : '复诊' }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="bloodPressure" label="血压"></el-table-column>
          <el-table-column align="center" prop="symptom" label="临床症状"></el-table-column>
          <el-table-column align="center" prop="fever" label="体温发热"></el-table-column>
          <el-table-column align="center" prop="epidemicDisease" label="流行病学史"></el-table-column>
          <!--<el-table-column align="center" prop="diagnosis" label="诊断"></el-table-column>-->
          <el-table-column align="center" prop="westernDiagnose,chinaDiagnose" label="诊断">
            <template slot-scope="scope">
              <span v-show="scope.row.westernDiagnose!=null">西医诊断:</span>
              {{ scope.row.westernDiagnose }}
              <span v-show="scope.row.westernDiagnose!=null&&scope.row.chinaDiagnose !=null">/</span>
              <span v-show="scope.row.chinaDiagnose!=null">中医诊断:</span>
              {{ scope.row.chinaDiagnose }}
            </template>
          </el-table-column>

          <el-table-column align="center" prop="infect" label="传染病">
            <template slot-scope="scope">
              <span>{{ scope.row.infect === "infectType_0" ? '否' : '是' }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="handle" label="处理情况"></el-table-column>
          <el-table-column align="center" prop="certificate" label="有效证件号"></el-table-column>
          <el-table-column align="center" prop="unit" label="工作单位"></el-table-column>
          <el-table-column align="center" prop="signature" label="医生签名"></el-table-column>
          <el-table-column align="center" prop="poverty" label="贫困标志">
            <template slot-scope="scope">
              <span v-show="scope.row.poverty!=null">贫</span>
            </template>
          </el-table-column>
          <el-table-column align="center" prop="telephone" label="联系电话"></el-table-column>
          <el-table-column align="center" prop="morbidityDate" label="发病日期"></el-table-column>
          <el-table-column align="center" prop="positiveResult" label="实验室阳性结果"></el-table-column>
          <el-table-column align="center" prop="healthEducation" label="个体化健康教育">
            <template slot-scope="scope">
              <span v-if="(scope.row.healthEducation) && scope.row.healthEducation.length<=10">{{scope.row.healthEducation}}</span>
              <el-tooltip v-else  effect="dark" :content="scope.row.healthEducation" placement="left">
                <span>{{showHealthEducation(scope.row.healthEducation)}}</span>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100px">
            <template slot-scope="scope">
              <!-- 放置修改、删除和分配角色按钮 -->
              <el-button
                size="mini"
                type="success" plain
                @click.native.stop="showEditDialog(scope.row.id)"
              >修改
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
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
    <!-- 修改用户的对话框 -->
    <el-dialog title="修改日志" :visible.sync="editDialogVisible" width="50%" @close="editDialogClosed"
               :close-on-click-modal='false'>
      <!-- 内容主体区 -->
      <el-form :model="editUserForm" ref="editUserFormRef" label-width="120px">
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="患者姓名:" prop="patientName">
              <el-input v-model="editUserForm.patientName"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="家长姓名:" prop="patriarchName">
              <el-input v-model="editUserForm.patriarchName"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2"><template slot-scope="scope">
            <span v-show="scope.row.poverty!=null">贫</span>
          </template>
            <el-form-item label="性别:" prop="sex">
                <el-select v-model="editUserForm.sex" placeholder="请选择性别">
                  <el-option
                    v-for="item in gender"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="患者年龄:" prop="age">
              <el-input v-model="editUserForm.age"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="民族:" prop="nation">
              <el-input v-model="editUserForm.nation"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="职业:" prop="occupation">
              <el-input v-model="editUserForm.occupation"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="详细地址:" prop="address">
              <el-input v-model="editUserForm.address"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="就诊日期:" prop="visitDate">
              <div class="block">
                <el-date-picker
                  v-model="editUserForm.visitDate"
                  type="datetime"
                  placeholder="选择日期时间"
                  align="right"
                  :picker-options="pickerOptions">
                </el-date-picker>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="初/复诊:" prop="initialVisit">
              <el-select v-model="editUserForm.initialVisit" placeholder="请选择">
                <el-option
                  v-for="item in initialVisit"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="血压:" prop="bloodPressure">
              <el-input v-model="editUserForm.bloodPressure"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="临床症状:" prop="symptom">
              <el-input v-model="editUserForm.symptom"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="体温发热:" prop="fever">
              <el-input v-model="editUserForm.fever"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="流行病学史:" prop="epidemicDisease">
              <el-input v-model="editUserForm.epidemicDisease"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="西医诊断:" prop="symptom">
              <el-input v-model="editUserForm.westernDiagnose"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="中医诊断:" prop="symptom">
              <el-input v-model="editUserForm.chinaDiagnose"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="处理情况:" prop="handle">
              <el-input v-model="editUserForm.handle"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="有效证件号:" prop="certificate">
              <el-input v-model="editUserForm.certificate"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="工作单位:" prop="unit">
              <el-input v-model="editUserForm.unit"/>
            </el-form-item>
          </el-col>
        </el-row>
          <!--<el-col :span="24 / 2">
            <el-form-item label="贫困标志:" prop="poverty">
              <el-input v-model="editUserForm.signature" disabled/>
            </el-form-item>
          </el-col>-->
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="联系电话:" prop="telephone">
              <el-input v-model="editUserForm.telephone"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="发病日期:" prop="morbidityDate">
              <div class="block">
                <el-date-picker
                  v-model="editUserForm.morbidityDate"
                  type="datetime"
                  placeholder="选择日期时间"
                  align="right"
                  :picker-options="pickerOptions">
                </el-date-picker>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 2">
            <el-form-item label="实验室阳性结果:" prop="positiveResult">
              <el-input v-model="editUserForm.positiveResult"/>
            </el-form-item>
          </el-col>
          <el-col :span="24 / 2">
            <el-form-item label="医生签名:" prop="signature">
              <el-input v-model="editUserForm.signature"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24 / 1">
            <el-form-item label="个体化健康教育:" prop="healthEducation">
              <el-input type="textarea" :autosize="{ minRows: 2, maxRows: 4}" v-model="editUserForm.healthEducation"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 底部区 -->
      <span slot="footer" class="dialog-footer">
     <el-button @click="editDialogVisible = false">取 消</el-button>
     <el-button type="primary" @click="editUser()">确 定</el-button>
     </span>
    </el-dialog>
    、
  </el-card>
</template>

<script>
  import {getStuffsalessummarylists, getStuffsalessummarysumss} from "@/api/toll/tollInfo";
  import {get, list, update,exportExcel} from "../../../api/toll/outpatientLog";
  import {BigNumber} from "bignumber.js";
  import RoleForm from "../../admin/role/roleForm";
  export default {

    data() {
      return {
        pickerOption: {
          shortcuts: [{
            text: '今天',
            onClick(picker) {
              picker.$emit('pick', new Date());
            }
          }, {
            text: '昨天',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit('pick', date);
            }
          }, {
            text: '一周前',
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', date);
            }
          }]
        },
        gender: [{ // 性别
          value: 'gender_0',
          label: '男'
        }, {
          value: 'gender_1',
          label: '女'
        }, {
        }],
        initialVisit: [{ // 性别
          value: 'registrationSource_0',
          label: '初诊'
        }, {
          value: 'registrationSource_1',
          label: '复诊'
        }, {
        }],
        queryInfo: {
          query: "",
          pagenum: 1, //当前的页数
          pagesize: 2, //每页的数量
        },
        userList: [],
        total: 0,
        addDialogVisible: false, //控制添加用户对话框的显示与隐藏
        editDialogVisible: false, //控制修改用户对话框的显示与隐藏
        //添加用户的表单数据
        addUserForm: {},
        //修改用户的表单数据
        editUserForm: {},
        show3: false,
        //时间选择
        TimeInterval: this.getInitializeDate(),
        formInline: {
          patientName: "",
          certificate: "",
        },

        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }]
        },

        tableData: [],

        StuffsalessummaryRc: {
          limit: 20,
          offset: 0,
          companyId: currentUser.company.id,
          kssj: "",
          jssj: "",
          patientName: "",
          certificate: "",
        },
        currentPage: 1,
        patientList: [],
        patientTotal: 0,
        allTotal: {},

      };
    },
    updated() {
      this.$nextTick(() => {
        this.$refs['tableData'].doLayout();
      })
    },
    methods: {
      showHealthEducation(conetnt){
        if (!conetnt) {
          return "";
        }
        return conetnt.substring(0, 10)+"...";
      },
      //关闭编辑用户的对话框
      editDialogClosed() {
        this.$refs.editUserFormRef.resetFields();
      },
      async showEditDialog(id) {
        const res = await get(id);
        this.editUserForm = res.data;
        console.log("res------------" + this.editUserForm.address)
        this.editDialogVisible = true;
      },
      //展示编辑用户的对话框
      /*showEditDialog(id) {
        this.editDialogVisible = true;
        console.log("id=====" + id);
      },*/
      stateFormat(row, column) {
        if (row.sex === "gender_0") {
          return '男'
        } else {
          return '女'
        }
        if (row.initialVisit === "registrationSource_0") {
          return '初诊'
        } else {
          return '复诊'
        }
      },
      //设置默认日期
      getInitializeDate() {
        let date = new Date()//获取新的时间
        //获取当前年份,并且转为字符串
        let year = date.getFullYear().toString()
        //获取当前月份，因为月份是要从0开始，此处要加1，
        //使用三元表达式，判断是否小于10，如果是的话，就在字符串前面拼接'0'
        let month = date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1).toString() : (date.getMonth() + 1).toString()
        //获取天，判断是否小于10，如果是的话，就在在字符串前面拼接'0'
        let day = date.getDate() < 10 ? '0' + date.getDate().toString() : date.getDate().toString()
        //字符串拼接，将开始时间和结束时间进行拼接
        let start = year + '-' + month + '-01' + ' 00:00:00'    //当月第一天
        //let end=new Date(year, month, 0).getDate();
        let end = year + '-' + month + '-' + day + ' 23:59:59'  //当天
        return [start, end] //将值设置给组件DatePicker 绑定的数据
      },
      //excel导出
      onExport(){
        // if (this.TimeInterval) {
        //   this.StuffsalessummaryRc.kssj = this.TimeInterval[0];
        //   this.StuffsalessummaryRc.jssj = this.TimeInterval[1]
        // }
        // this.StuffsalessummaryRc.patientName = this.formInline.patientName;
        // this.StuffsalessummaryRc.certificate = this.formInline.certificate;
        exportExcel(this.StuffsalessummaryRc).then((res)=>{
          // const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
          const filename = '门诊日志.xlsx'
          const blob = new Blob([res.data], {
            type: 'application/octet-stream'
          })

          let url = window.URL.createObjectURL(blob);

          let link = document.createElement('a');

          link.style.display = 'none';

          link.href = url;

          link.setAttribute('download', filename);

          document.body.appendChild(link);

          link.click()
        }).catch((error)=>{
          this.outputError(error)
        })
      },
      onSizeChange(val) {
        this.currentPage = 1
        this.StuffsalessummaryRc.limit = val;
        this.StuffsalessummaryRc.offset = (this.currentPage - 1) * val
        this.Getypjxclist();
      },
      onCurrentChange(val) {
        this.StuffsalessummaryRc.offset = (val - 1) * this.StuffsalessummaryRc.limit
        this.currentPage = val
        this.Getypjxclist();
      },
      indexMethod(index) {
        return (this.currentPage - 1) * this.StuffsalessummaryRc.limit + index + 1;
      },
      resetCondition() {
        this.StuffsalessummaryRc = {
          limit: 20,
          offset: 0,
          companyId: currentUser.company.id,
          kssj: "",
          jssj: "",
          patientName: "",
          certificate: "",
        },
          this.formInline = {
            patientName: "",
            certificate: "",
          },
          this.TimeInterval = this.getInitializeDate(),
          this.Getypjxclist();
      },
      orderbyChange() {
        this.Getypjxclist();
      },
      Getypjxclist() {
        this.tableData = [];
        if (this.TimeInterval) {
          this.StuffsalessummaryRc.kssj = this.TimeInterval[0];
          this.StuffsalessummaryRc.jssj = this.TimeInterval[1]
        }
        this.StuffsalessummaryRc.patientName = this.formInline.patientName;
        this.StuffsalessummaryRc.certificate = this.formInline.certificate;
        list(this.StuffsalessummaryRc).then((responseData) => {
          if (responseData.code == 100) {
            this.patientTotal = responseData.data.total;
            if (responseData.data.total > 0) {
              responseData.data.rows.forEach((item) => {
                this.tableData.push({
                  id: item.id,  //患者id
                  patientName: item.patientName,  //患者姓名
                  patriarchName: item.patriarchName,  //家长姓名
                  sex: item.sex,  //性别
                  age: item.age,  //患者年龄
                  nation: item.nation,  //民族
                  occupation: item.occupation,  //职业
                  address: item.address,  //详细地址
                  visitDate: item.visitDate,  //就诊日期
                  initialVisit: item.initialVisit,  //初/复诊
                  bloodPressure: item.bloodPressure,  //血压
                  symptom: item.symptom,  //临床症状
                  fever: item.fever,  //体温发热
                  epidemicDisease: item.epidemicDisease,  //流行病学史
                  westernDiagnose: item.westernDiagnose,  //西医诊断
                  chinaDiagnose: item.chinaDiagnose,  //中医诊断
                  diagnosis: item.diagnosis,  //诊断
                  infect: item.infect,  //传染病
                  handle: item.handle,  //处理情况
                  certificate: item.certificate,  //有效证件号
                  unit: item.unit,  //工作单位
                  signature: item.signature,  //医生签名
                  poverty: item.poverty,  //贫困标志
                  telephone: item.telephone,  //联系电话
                  morbidityDate: item.morbidityDate,  //发病日期
                  positiveResult: item.positiveResult,  //实验室阳性结果
                  healthEducation: item.healthEducation  //个体化健康教育
                })
              })

              getStuffsalessummarysumss(this.StuffsalessummaryRc).then((ref) => {
                if (ref.data.total > 0) {
                  this.allTotal = ref.data.rows[0];
                }

              })
            }
          }
        })
      },
      //点击按钮，修改用户信息
      editUser(){
        this.$refs.editUserFormRef.validate(async valid =>{
          if(!valid) return;//校验没通过，返回
          //可以发起修改用户的网络请求
          await update(this.editUserForm).then(response => {
            this.$message.success('修改用户成功！');
            // 隐藏修改用户的对话框
            this.editDialogVisible = false;
            //重新获取用户列表数据
            this.Getypjxclist();
          })

        })
      },
      onSubmit() {
        this.Getypjxclist();
      },
    },

    mounted() {
      this.Getypjxclist();
    }
  };
</script>
<style lang="scss">
  .el-picker-panel__footer .el-picker-panel__link-btn.el-button--text {
    display: none;
  }

  .el-card__body {
    padding: 0px;
  }
</style>

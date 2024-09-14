<template>
  <div>
    <el-container>
      <test-form ref="testForm"></test-form>
      <el-main>
        <el-tabs
          v-model="activeName"
          type="border-card"
          @tab-click="handleClick"
          style="width: 100%; height: 100%"
        >
          <el-tab-pane label="预约总览" name="schedulingzl">
            <span slot="label">预约总览 </span>

            <el-row>
              <el-radio-group
                v-model="tabPosition"
                style="margin-bottom: 30px"
                @change="changeTheme"
              >
                <el-radio-button label="0">周</el-radio-button>
                <el-radio-button label="1">日</el-radio-button>
              </el-radio-group>
            </el-row>
            <el-row>
              <el-col :span="12">
                <input
                  type="text"
                  v-model="week_time"
                  disabled
                  style="border-style: none; font-weight: 700; font-size: 24px"
                  autocomplete="“off”"
                />
              </el-col>
              <el-col :span="12" v-if="tabPosition == '0'">
                <div style="float: right">
                  <el-button
                    @click="schedulingzlDayList(-(7 * 24 * 60 * 60 * 1000))"
                    >上一周</el-button
                  >
                  <el-button @click="schedulingzlDayList()">本周</el-button>
                  <el-button
                    @click="schedulingzlDayList(+(7 * 24 * 60 * 60 * 1000))"
                    >下一周</el-button
                  >
                </div>
              </el-col>
               <el-col :span="12" v-if="tabPosition == '1'">
                <div style="float: right">
                  <el-button
                    @click="schedulingzlrdaysList(-(86400000))"
                    >上一天</el-button
                  >
                  <el-button @click="schedulingzlrdaysList()">当天</el-button>
                  <el-button
                    @click="schedulingzlrdaysList(+(86400000))"
                    >下一天</el-button
                  >
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-table
                v-if="tabPosition == '0'"
                :data="schedulingzlday"
                border
                height="100%"
                :span-method="objectSpanMethod"
                style="
                  width: 100%;
                  text-align: center;
                  border: 1px solid #d9d9d9;
                "
                :cell-style="{ background: '#ffffff' }"
                :header-cell-style="{
                  background: '#ffffff',
                  color: '#333',
                  fontSize: '13px',
                  'text-align': 'center',
                }"
              >
                <el-table-column
                  prop="dayname"
                  label="日期"
                  width="100"
                ></el-table-column>
                <el-table-column prop="timename" label="时间段" width="180">
                  <template slot-scope="scope">
                    <span>
                      {{ scope.row.timename }} {{ scope.row.timestrea }} -
                      {{ scope.row.timeend }}
                    </span>
                  </template>
                </el-table-column>

                <el-table-column
                  prop="userid"
                  label="序号"
                  v-if="false"
                  width="180"
                >
                </el-table-column>
                <el-table-column
                  v-for="(item, index) in schedulingzlList"
                  :key="index"
                  :prop="item.prop"
                  :label="item.username + '(' + item.ksname + ')'"
                  width="130"
                >
                  <template slot-scope="scope">
                    <!-- <el-button plain  @click="rowbutton(scope.row[item.prop], item,scope.row)">{{scope.row}}</el-button> -->
                    <!-- scope.row.newfalg==1 &&
                      (scope.row.timestreaint < scope.row.newdatetime &&
                      scope.row.timeendint > scope.row.newdatetime ||
                      scope.row.timestreaint > scope.row.newdatetime &&
                      scope.row.timeendint > scope.row.newdatetime) -->
                    <span
                      v-if="
                        item.datelist[get(scope.row.day)] != null &&
                        scope.row.newfalg == 1 &&
                        ((scope.row.timestreaint <= scope.row.newdatetime &&
                          scope.row.timeendint >= scope.row.newdatetime) ||
                          (scope.row.timestreaint > scope.row.newdatetime &&
                            scope.row.timeendint > scope.row.newdatetime))
                      "
                    >
                      <!-- <el-button plain  @click="rowbutton(scope.row[item.prop], item,scope.row)">新增</el-button> -->
                      <el-button
                        plain
                        style="width: 42%"
                        @click="
                          rowbutinsert(
                            item.datelist[get(scope.row.day)],
                            item,
                            scope.row
                          )
                        "
                        >新增</el-button
                      >
                      <el-button
                        plain
                        style="width: 42%"
                        @click="
                          rowbutseach(
                            item.datelist[get(scope.row.day)],
                            item,
                            scope.row
                          )
                        "
                        >明细</el-button
                      >
                    </span>
                    <span
                      v-else-if="
                        item.datelist[get(scope.row.day)] != null &&
                        Date.parse(item.datelist[get(scope.row.day)]) >
                          Date.parse(scope.row.thecurrent)
                      "
                      ><el-button
                        plain
                        style="width: 42%"
                        @click="
                          rowbutinsert(
                            item.datelist[get(scope.row.day)],
                            item,
                            scope.row
                          )
                        "
                        >新增</el-button
                      >
                      <el-button
                        plain
                        style="width: 42%"
                        @click="
                          rowbutseach(
                            item.datelist[get(scope.row.day)],
                            item,
                            scope.row
                          )
                        "
                        >明细</el-button
                      >
                    </span>
                  </template>
                </el-table-column>
              </el-table>
            </el-row>
            <el-row>
              <el-table
                v-if="tabPosition == '1'"
                :data="schedulingzlrdayList"
                style="width: 100%"
              >
                <el-table-column prop="label" label="时间" width="180">
                </el-table-column>
                <el-table-column v-for="(item, index) in schedulingzlrday"
                  :key="index"
                  :prop="item.prop"
                  :label="item.username + '(' + item.ksname + ')'"
                  width="130">
                  <template slot-scope="scope">
                    <el-button v-if="getlist(item.datehouse,scope.row.strattime,scope.row.endtime)!=0" @click="rdaybutton(scope.row)" >明细</el-button>
                  </template>

                </el-table-column>

              </el-table>
            </el-row>

          </el-tab-pane>
          <el-tab-pane label="预约明细" name="schedulingmx">
            <span slot="label">预约明细 </span>
            <el-row>
              <el-form ref="form" :model="schedulingmxseachmodel">
                <el-col :span="8">
                  <el-form-item label="预约时间" prop="pickervalues">
                    <el-date-picker
                      v-model="schedulingmxseachmodel.pickervalues"
                      type="daterange"
                      style="width: 180px"
                      value-format="yyyy-MM-dd HH:mm"
                      format="MM/dd"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      :default-time="['00:00:00', '23:59:59']"
                    >
                    </el-date-picker>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="状态" prop="status">
                    <el-select
                      v-model="schedulingmxseachmodel.status"
                      value-key="id"
                      filterable
                      clearable
                      placeholder="请选择登记状态"
                    >
                      <el-option
                        v-for="status in statusList"
                        :key="status.id"
                        :label="status.name"
                        :value="status"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="患者信息" prop="patientname">
                    <input
                      type="text"
                      style="width: 140px"
                      v-model="schedulingmxseachmodel.patientname"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="4">
                  <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="schedulingmxdayList()"
                    :plain="true"
                    >搜索</el-button
                  >
                </el-col>
              </el-form>
            </el-row>
            <el-row>
              <el-table
                height="500"
                style="
                  width: 100%; height:100%;
                  text-align: center;
                  border: 1px solid #d9d9d9;
                "
                :data="schedulingmxList"
              >
                <el-table-column
                  property="name"
                  label="患者"
                  width="120"
                ></el-table-column>
                <el-table-column
                  property="gendername"
                  label="性别"
                  width="60"
                ></el-table-column>
                <el-table-column
                  property="age"
                  label="年龄"
                  width="60"
                ></el-table-column>
                <el-table-column
                  property="ksname"
                  label="预约科室"
                  width="120"
                ></el-table-column>
                <el-table-column
                  property="ysname"
                  label="预约员工"
                  width="120"
                ></el-table-column>
                <el-table-column
                  property="sourcename"
                  label="预约来源"
                ></el-table-column>
                <el-table-column
                  property="subscribedate"
                  label="预约时间"
                  width="200"
                ></el-table-column>
                <el-table-column
                  property="statusname"
                  label="预约状态"
                ></el-table-column>
              </el-table>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="排班" name="scheduling">
            <span slot="label">排班 </span>
            <el-row>
              <el-col :span="12">
                <input
                  type="text"
                  v-model="week_time"
                  disabled
                  style="border-style: none; font-weight: 700; font-size: 24px"
                  autocomplete="“off”"
                />
              </el-col>
              <el-col :span="12">
                <div style="float: right">
                  <el-button
                    @click="schedulingDayList(-(7 * 24 * 60 * 60 * 1000))"
                    >上一周</el-button
                  >
                  <el-button @click="schedulingDayList()">本周</el-button>
                  <el-button
                    @click="schedulingDayList(+(7 * 24 * 60 * 60 * 1000))"
                    >下一周</el-button
                  >
                </div>
              </el-col>
            </el-row>
            <el-row>
              <el-table
                ref="table"
                :key="tableKey"
                v-loading="loading"
                :empty-text="emptyText"
                :data="schedulingList"
                height="400"
                border
                fit
              >
                <el-table-column
                  prop="userid"
                  label="员工ID"
                  v-if="false"
                  width="180"
                >
                </el-table-column>
                <el-table-column prop="username" label="员工" fixed width="180">
                  <template slot-scope="scope">
                    {{ scope.row.username }}({{ scope.row.ksname }})
                  </template>
                </el-table-column>
                <el-table-column
                  v-for="(item, index) in formThead"
                  :key="index"
                  :prop="item.key"
                  :label="item.label + '(' + item.weekName + ')'"
                  width="180"
                >
                  <!-- 动态表格 -->

                  <template slot-scope="scope">
                    <template v-for="(column, index) in scope.row.dates">
                      <el-select
                        v-model="column[item.key]"
                        @change="rowdata(column[item.key], item.key, scope.row)"
                        clearable
                        @clear="
                          delrowdata(column[item.key], item.key, scope.row)
                        "
                        placeholder="请选择"
                        :key="index"
                      >
                        <el-option
                          v-for="items in options"
                          :key="items.value"
                          :label="items.label"
                          :value="items.value"
                        >
                        </el-option>
                      </el-select>
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
    <el-dialog
      width="70%"
      height="60%"
      title="预约信息"
      :visible.sync="dialogTableVisible"
    >
      <el-table width="100%" height="80%" :data="schedulingmxList">
        <el-table-column
          property="name"
          label="患者"
          width="150"
        ></el-table-column>
        <el-table-column
          property="gendername"
          label="性别"
          width="200"
        ></el-table-column>
        <el-table-column
          property="age"
          label="年龄"
          width="200"
        ></el-table-column>
        <el-table-column
          property="ksname"
          label="预约科室"
          width="200"
        ></el-table-column>
        <el-table-column
          property="ysname"
          label="预约员工"
          width="200"
        ></el-table-column>
        <el-table-column
          property="sourcename"
          label="预约来源"
        ></el-table-column>
        <el-table-column
          property="subscribedate"
          label="预约时间"
          width="200"
        ></el-table-column>
        <el-table-column
          property="statusname"
          label="预约状态"
        ></el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogTableVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  listscheduling,
  editSave,
  editDelete,
  listscheduzlling,
  listschedumxling,
  listzlrday,
} from "@/api/outpatient/scheduling";
import { listDictItemAll } from "@/api/sys/dictItem";
import MainUI from "@/views/components/mainUI";
import History from "@/views/components/history";
import TestForm from "./schedulingForm";
export default {
  extends: MainUI,
  components: {
    TestForm,
    History,
  },
  data() {
    return {
      activeName: "schedulingzl",
      datetimebutton: null,
      page: 1,
      rows: 10,
      total: 12,
      weekdaylist: [
        "星期一",
        "星期二",
        "星期三",
        "星期四",
        "星期五",
        "星期六",
        "星期天",
      ],
      weekproplist: [
        "monday",
        "tuesday",
        "wednesday",
        "thursday",
        "friday",
        "saturday",
        "sunday",
      ],
      housrslist : [7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24],
      flagchange: true,
      dateweeklist: [],
      //预约总览
      schedulingzlList: [],
      schedulingzlday: [],
      schedulingzlrdayList: [],
      schedulingzlrday:[],
      //预约明细
      schedulingmxList: [],
      schedulingmxseachmodel: {
        pickervalues: [], //预约明细时间条件
        status: null,
        patientname: null,
      },
      statusList: [],
      //排班
      schedulingList: [],
      listLoading: false,
      tableKey: 0,
      emptyText: "暂无数据",
      formThead: [],
      dayList: [],
      lists: [],
      week_time: "",
      weekDates: [],
      schedulingoption: [],
      tabPosition: "0",
      options: [
        {
          value: "998324809623056666",
          label: "全天",
        },
        {
          value: "998324809623057777",
          label: "上午",
        },
        {
          value: "998324809623058888",
          label: "下午",
        },
      ],
      tableData: [],

      tableData1: [
        {
          date: "2016-05-02",
          name: "张三",
          address: "上海市普陀区金沙江路 1518 弄",
        },
        {
          date: "2016-05-04",
          name: "张三",
          address: "上海市普陀区金沙江路 1517 弄",
        },
        {
          date: "2016-05-01",
          name: "张三",
          address: "上海市普陀区金沙江路 1519 弄",
        },
        {
          date: "2016-05-03",
          name: "张三",
          address: "上海市普陀区金沙江路 1516 弄",
        },
      ],
      seachmodel: {
        userid: null,
        schedulingtime: null,
        pbid: null,
        stratime: null,
        endtime: null,
        companyid: null,
        datestra: null,
        dateend: null,
        ksid: null,
        status: null,
        username: null,
        patientname:null,
      },
      schedulingsave: {
        companyid: null,
        userid: null,
        schedulingtime: null,
        timeid: null,
      },
      dialogTableVisible: false, //明细弹框
    };
  },
  mounted() {
    this.schedulingzlDayList();
    //this.schedulingmxdayList();
    //this.tableDatas();
  },
  methods: {
    get(row) {
      let count = this.weekproplist.findIndex((role) => role === row);
      return count;
    },
    getlist(list,strat,end){
      let flg=0;
      if(list&&list.length>0){
        for(let i=0;i<list.length;i++){
          let hoser= this.housrslist[this.housrslist.findIndex((role) => role === list[i])];
          if(strat<=hoser&&end>hoser){
            return hoser;
          }
        }
      }else{
        flg=0;
      }
      return flg;
    },
    removeseachmodel(){
      this.seachmodel={
        userid: null,
        schedulingtime: null,
        pbid: null,
        stratime: null,
        endtime: null,
        companyid: null,
        datestra: null,
        dateend: null,
        ksid: null,
        status: null,
        username: null,
        patientname:null,
      };
    },
    schedulingmxdaylistcreate() {
      this.schedulingmxseachmodel = {
        pickervalues: [], //预约明细时间条件
        status: null,
        patientname: null,
      };
    },
    handleClick(tab, event) {
       //this.$message.success(this.tabPosition);
      if (tab.name == "scheduling") {
        this.schedulingDayList();
        this.schedulingmxdaylistcreate();
      } else if (tab.name == "schedulingmx") {
        let paramsc = {
          params: [
            {
              columnName: "dict_type_id",
              queryType: "=",
              value: "1008898177293385773",
            },
          ],
          offset: 0,
          limit: 100,
          columnName: "", // 排序字段名
          order: "", // 排序
        };
        listDictItemAll(paramsc).then((responseData) => {
          this.statusList = responseData.data;
          console.log(this.statusList, + "sss");
        });
        this.schedulingmxdayList();
      } else if (tab.name == "schedulingzl") {
        this.tabPosition='0';
        this.schedulingzlDayList();
        this.schedulingmxdaylistcreate();
      }
      //console.log(tab, event);
    },
    //排班新增功能 选中下拉框
    rowdata(data, index, rows) {
      if (data) {
        // console.log(data);
        // console.log(index);
        let count = this.weekproplist.findIndex((role) => role === index);
        // console.log(this.dateweeklist[count]);
        // console.log(rows.userid);
        let savemodel = this.schedulingsave;
        savemodel.companyid = currentUser.company.id;
        savemodel.schedulingtime = Date.parse(this.dateweeklist[count]);
        savemodel.timeid = data;
        savemodel.userid = rows.userid;
        console.log(savemodel)
        editSave(savemodel)
          .then((responseData) => {
            //this.flage = false;
            if (responseData.code == 100) {
              //console.log(responseData.data);
              if (responseData.data) {
                this.$message.success("排班操作成功");
              }
            } else {
              //this.showMessage(responseData)
            }
            //this.resetLoad()
          })
          .catch((error) => {
            //this.flage = false;
            //this.outputError(error)
          });
      }
    },
    //排班撤销功能 删除下拉框选中内容
    delrowdata(data, index, rows) {
      // console.log("crela:" + data);
      // console.log(index);
      let count = this.weekproplist.findIndex((role) => role === index);
      // console.log(this.dateweeklist[count]);
      // console.log(rows.userid);

      let deletemodel = this.schedulingsave;
      deletemodel.companyid = currentUser.company.id;
      deletemodel.schedulingtime = this.dateweeklist[count];
      deletemodel.userid = rows.userid;
      editDelete(deletemodel)
        .then((responseData) => {
          //this.flage = false;
          if (responseData.code == 100) {
            console.log(responseData.data);
            if (responseData.data && responseData.data > 0) {
              this.$message.success("撤销排班成功");
            }
          } else {
            //this.showMessage(responseData)
          }
          //this.resetLoad()
        })
        .catch((error) => {
          //this.flage = false;
          //this.outputError(error)
        });
    },
    //周表格中新增按钮事件
    rowbutinsert(val, itemdata, rowdata) {
      console.log("click:" + val);

      let listarray = {
        ...itemdata,
        ...rowdata,
      };
      console.log(itemdata,'新增');
      this.$refs.testForm.$emit("openAddRegistrationDialog", listarray);
    },
    //周表格中明细弹框显示
    rowbutseach(val, itemdata, rowdata) {
      this.schedulingmxList=[];
      this.dialogTableVisible = true;
      let seachmode = this.seachmodel;
      seachmode.companyid = currentUser.company.id;
      seachmode.datestra = rowdata.fn + " " + rowdata.timestrea;
      seachmode.dateend = rowdata.fn + " " + rowdata.timeend;
      seachmode.userid = itemdata.userid;
      seachmode.ksid = itemdata.ksid;
      // console.log("mx",val);
      // console.log("mx",itemdata);
      // console.log("mx",rowdata);
      this.removeseachmodel();
      listschedumxling(seachmode)
        .then((responseData) => {
          //this.flage = false;
          if (responseData.code == 100) {
            console.log(responseData.data);
            this.schedulingmxList = responseData.data;
            // if (responseData.data && responseData.data > 0) {
            //   this.$message.success("撤销排班成功");
            // }
          } else {
            //this.showMessage(responseData);
            this.$message.success(responseData);
          }
          //this.resetLoad()
        })
        .catch((error) => {
          this.$message.success(error);
          //this.flage = false;
          //this.outputError(error)
        });
    },
    //日表格中明细弹框显示
    rdaybutton(itemdata){
      this.schedulingmxList=[];
      this.dialogTableVisible = true;
      let seachmode = this.seachmodel;
      seachmode.companyid = currentUser.company.id;
      seachmode.datestra = itemdata.daydate + " " + itemdata.timestrea;
      seachmode.dateend = itemdata.daydate + " " + itemdata.timeend;
      seachmode.userid = itemdata.userid;
      seachmode.ksid = itemdata.ksid;
      this.removeseachmodel();
      listschedumxling(seachmode)
        .then((responseData) => {
          //this.flage = false;
          if (responseData.code == 100) {
            console.log(responseData.data);
            this.schedulingmxList = responseData.data;
            // if (responseData.data && responseData.data > 0) {
            //   this.$message.success("撤销排班成功");
            // }
          } else {
            //this.showMessage(responseData);
            this.$message.success(responseData);
          }
          //this.resetLoad()
        })
        .catch((error) => {
          this.$message.success(error);
          //this.flage = false;
          //this.outputError(error)
        });

    },
    //周和日表格切换点击事件
    changeTheme(val) {
      //周和日表格切换
      if (val == "0") {
        this.schedulingzlDayList();
      } else {
        this.schedulingzlrdaysList();
      }
    },
    handleSizeChange(val) {
      // console.log( this.tableData );
    },
    //预约总览表格周初始化数据
    schedulingzlDayList(val) {
      this.schedulingzlday = [];
      this.dateweeklist = [];
      this.schedulingzlList=[];
      // 动态添加天数及对应星期几
      let tableprop = 0;

      let time = new Date();
      let timesStamp;
      if (val) {
        if (this.datetimebutton) {
          this.datetimebutton += val;
        } else {
          this.datetimebutton = val;
        }
        timesStamp = time.getTime() + this.datetimebutton;
      } else {
        timesStamp = time.getTime();
        this.datetimebutton = null;
      }

      let currenDay = time.getDay();
      //获取当前日期的星期 开始和结束  strat
      let newtime1 = new Date(
        timesStamp + 24 * 60 * 60 * 1000 * (0 - ((currenDay + 6) % 7))
      )
        .toLocaleDateString()
        .replace(/\//g, "-");
      let newtime2 = new Date(
        timesStamp + 24 * 60 * 60 * 1000 * (6 - ((currenDay + 6) % 7))
      )
        .toLocaleDateString()
        .replace(/\//g, "-");
      let newdates1 = new Date(newtime1);
      let newdates2 = new Date(newtime2);
      let vardates =
        newdates1.getFullYear() +
        "-" +
        (newdates1.getMonth() + 1) +
        "-" +
        (newdates1.getDate() < 10
          ? "0" + newdates1.getDate()
          : newdates1.getDate());
      let vardates1 =
        newdates1.getFullYear() +
        "-" +
        (newdates1.getMonth() + 1) +
        "-" +
        (newdates1.getDate() < 10
          ? "0" + newdates1.getDate()
          : newdates1.getDate());
      let vardates2 =
        newdates2.getFullYear() +
        "-" +
        (newdates2.getMonth() + 1) +
        "-" +
        (newdates2.getDate() < 10
          ? "0" + newdates2.getDate()
          : newdates2.getDate());

      //获取当前日期的星期 开始和结束  end

      let endtime =
        newdates2.getMonth() +
        1 +
        "/" +
        (newdates2.getDate() < 10
          ? "0" + newdates2.getDate()
          : newdates2.getDate());
      this.week_time = vardates1 + "-" + endtime;

      let seachmode = this.seachmodel;
      seachmode.stratime = vardates1;
      seachmode.endtime = vardates2;
      seachmode.companyid = currentUser.company.id;
      this.removeseachmodel();
      listscheduzlling(seachmode)
        .then((responseData) => {
          //this.flage = false;
          if (responseData.code == 100) {
            console.log(responseData.data,'11111111111111111111111111');
            this.schedulingzlList = responseData.data;
          } else {
            //this.showMessage(responseData)
          }
          //this.resetLoad()
        })
        .catch((error) => {
          //this.flage = false;
          //this.outputError(error)
        });

      let nowDate = new Date(Date.parse(new Date()));
      let nowDay =
        nowDate.getDate() < 10 ? "0" + nowDate.getDate() : nowDate.getDate(); //日
      let nowmonth = nowDate.getMonth() + 1; //月
      let yeare = nowDate.getFullYear(); //年
      let hh = nowDate.getHours(); //小时
      let thecurrent = yeare + "-" + nowmonth + "-" + nowDay;
      let daytime = [
        {
          timename: "上午",
          timestrea: "08:00",
          timeend: "12:00",
          timestreaint: 8,
          timeendint: 12,
        },
        {
          timename: "下午",
          timestrea: "12:00",
          timeend: "18:00",
          timestreaint: 12,
          timeendint: 18,
        },
        {
          timename: "傍晚",
          timestrea: "18:00",
          timeend: "24:00",
          timestreaint: 18,
          timeendint: 24,
        },
      ];

      for (let i = 0; i < 7; i++) {
        let newtime = new Date(
          timesStamp + 24 * 60 * 60 * 1000 * (i - ((currenDay + 6) % 7))
        )
          .toLocaleDateString()
          .replace(/\//g, "-");
        let newdates = new Date(newtime);
        let vardates =
          newdates.getFullYear() +
          "-" +
          (newdates.getMonth() + 1) +
          "-" +
          (newdates.getDate() < 10
            ? "0" + newdates.getDate()
            : newdates.getDate());

        //console.log("循环星期:"+vardates);
        this.dateweeklist.push(vardates);
        for (let k = 0; k < 3; k++) {
          let dayNum = {
            dayname: vardates.slice(5) + "(" + this.weekdaylist[i] + ")",
            timename: daytime[k].timename,
            timestrea: daytime[k].timestrea,
            timeend: daytime[k].timeend,
            fn: vardates,
            day: this.weekproplist[i],
            timestreaint: daytime[k].timestreaint,
            timeendint: daytime[k].timeendint,
            newdatetime: hh,
            newfalg:
              Date.parse(vardates) == Date.parse(thecurrent)
                ? 1
                : Date.parse(vardates) < Date.parse(thecurrent)
                ? 0
                : 2,
            thecurrent: thecurrent,
          };

          this.schedulingzlday.push(dayNum);
        }
      }
      console.log("schedulingzldaylist加载");
      console.log(this.schedulingzlday);
      let contactDot = 0;
      let contactDot_1 = 0;
      this.spanArr = [];
      this.contentSpanArr = [];
      this.schedulingzlday.forEach((item, index) => {
        item.index = index;
        if (index === 0) {
          this.spanArr.push(1);
          this.contentSpanArr.push(1);
        } else {
          // 判断第二列
          if ((item.index + 1) % 3 != 1) {
            this.spanArr[contactDot] += 1;
            this.spanArr.push(0);
          } else {
            this.spanArr.push(1);
            contactDot = index;
          }
          //判断第三列
          if (false) {
            this.contentSpanArr[contactDot_1] += 1;
            this.contentSpanArr.push(0);
          } else {
            this.contentSpanArr.push(1);
            contactDot_1 = index;
          }
        }
      });
    },
    //预约总览周表格合并列
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        const _row = this.spanArr[rowIndex];
        const _col = _row > 0 ? 1 : 0;
        return {
          rowspan: _row,
          colspan: _col,
        };
      }
      if (columnIndex === 1) {
        const _row = this.contentSpanArr[rowIndex];
        const _col = _row > 0 ? 1 : 0;
        return {
          rowspan: _row,
          colspan: _col,
        };
      }
    },
    //预约总览日表格初始化数据
    schedulingzlrdaysList(val) {
      this.schedulingzlrdayList=[];
      this.schedulingzlrday=[];
      let newDate=new Date();
      let nowDate
       if (val) {
        if (this.datetimebutton) {
          this.datetimebutton += val;
        } else {
          this.datetimebutton = val;
        }
        nowDate= new Date(newDate.getTime() + this.datetimebutton);

      } else {
        nowDate= new Date(newDate.getTime());
        this.datetimebutton = null;
      }

      let nowDay =nowDate.getDate() < 10 ? "0" + nowDate.getDate() : nowDate.getDate(); //日
      let nowmonth =
        nowDate.getMonth() + 1 < 10
          ? "0" + nowDate.getMonth() + 1
          : nowDate.getMonth() + 1; //月
      let yeare = nowDate.getFullYear(); //年
      this.week_time = yeare + "/" + nowmonth + "/" + nowDay;
      let hh = nowDate.getHours(); //小时
      let housrs = this.housrslist;
      for (let i = 0; i < housrs.length - 1; i++) {
        let daylist = {
          label: housrs[i] + ":00 ~ " + housrs[i + 1] + ":00",
          strattime: housrs[i],
          endtime: housrs[i + 1],
          daydate: yeare + "-" + nowmonth + "-" + nowDay,
          timestrea:housrs[i] + ":00",
          timeend: housrs[i + 1] + ":00",
        };
        this.schedulingzlrdayList.push(daylist);
      }

      let seachmodes = this.seachmodel;
      seachmodes.companyid = currentUser.company.id;
      seachmodes.datestra = yeare + "-" + nowmonth + "-" + nowDay+" 00:00";
      seachmodes.dateend = yeare + "-" + nowmonth + "-" + nowDay+" 23:59";
      console.log("rl",seachmodes,this.seachmodel)
      this.removeseachmodel();
      listzlrday(seachmodes)
        .then((responseData) => {
          if (responseData.code == 100) {
            console.log(responseData.data);
            this.schedulingzlrday = responseData.data;
          } else {
            this.$message.success(responseData);
          }
        })
        .catch((error) => {
          this.$message.success(error);
        });
    },
    //预约明细表格初始化数据
    schedulingmxdayList() {
      this.schedulingmxList=[];
      let seachmode = this.seachmodel;
      seachmode.companyid = currentUser.company.id;
      if (this.schedulingmxseachmodel.pickervalues) {
        seachmode.datestra = this.schedulingmxseachmodel.pickervalues[0];
        seachmode.dateend = this.schedulingmxseachmodel.pickervalues[1];
      }
      if (this.schedulingmxseachmodel.status) {
        seachmode.status = this.schedulingmxseachmodel.status.value;
      }
      if (this.schedulingmxseachmodel.patientname) {
        seachmode.patientname = this.schedulingmxseachmodel.patientname;
      }
      console.log("ccc", seachmode);
      this.removeseachmodel();
      listschedumxling(seachmode)
        .then((responseData) => {
          if (responseData.code == 100) {
            console.log(responseData.data);
            this.schedulingmxList = responseData.data;
          } else {
            this.$message.success(responseData);
          }
        })
        .catch((error) => {
          this.$message.success(error);
        });
    },
    //排班表格初始化数据
    schedulingDayList(val) {
      this.schedulingList = []; //先清空数据
      this.dateweeklist = [];
      this.formThead = [];
      // 动态添加天数及对应星期几
      let nowDate = new Date(Date.parse(new Date()));
      let nowDay = nowDate.getDate(); //日
      let nowmonth = nowDate.getMonth() + 1; //月
      let yeare = nowDate.getFullYear(); //年
      let nowDayss = nowDate.getDate() - nowDate.getDay() + 1; //星期

      let time = new Date();
      let timesStamp;
      if (val) {
        if (this.datetimebutton) {
          this.datetimebutton += val;
        } else {
          this.datetimebutton = val;
        }
        timesStamp = time.getTime() + this.datetimebutton;
      } else {
        timesStamp = time.getTime();
        this.datetimebutton = null;
      }
      let currenDay = time.getDay();
      //获取当前日期的星期 开始和结束  strat
      let newtime1 = new Date(
        timesStamp + 24 * 60 * 60 * 1000 * (0 - ((currenDay + 6) % 7))
      )
        .toLocaleDateString()
        .replace(/\//g, "-");
      let newtime2 = new Date(
        timesStamp + 24 * 60 * 60 * 1000 * (6 - ((currenDay + 6) % 7))
      )
        .toLocaleDateString()
        .replace(/\//g, "-");
      let newdates1 = new Date(newtime1);
      let newdates2 = new Date(newtime2);
      let vardates =
        newdates1.getFullYear() +
        "-" +
        (newdates1.getMonth() + 1) +
        "-" +
        (newdates1.getDate() < 10
          ? "0" + newdates1.getDate()
          : newdates1.getDate());
      let vardates1 =
        newdates1.getFullYear() +
        "-" +
        (newdates1.getMonth() + 1) +
        "-" +
        (newdates1.getDate() < 10
          ? "0" + newdates1.getDate()
          : newdates1.getDate());
      let vardates2 =
        newdates2.getFullYear() +
        "-" +
        (newdates2.getMonth() + 1) +
        "-" +
        (newdates2.getDate() < 10
          ? "0" + newdates2.getDate()
          : newdates2.getDate());

      //获取当前日期的星期 开始和结束  end
      //console.log(vardates+"||"+vardates2);
      let stratime = vardates1;
      let enddate = vardates2;
      let seachmode = this.seachmodel;
      seachmode.stratime = stratime;
      seachmode.endtime = enddate;
      seachmode.companyid = currentUser.company.id;
      listscheduling(seachmode)
        .then((responseData) => {
          //this.flage = false;
          if (responseData.code == 100) {
            console.log("cc", responseData.data);
            this.schedulingList = responseData.data;
          } else {
            //this.showMessage(responseData)
          }
          //this.resetLoad()
        })
        .catch((error) => {
          //this.flage = false;
          //this.outputError(error)
        });

      for (let i = 0; i < 7; i++) {
        let newtime = new Date(
          timesStamp + 24 * 60 * 60 * 1000 * (i - ((currenDay + 6) % 7))
        )
          .toLocaleDateString()
          .replace(/\//g, "-");
        let newdates = new Date(newtime);
        let vardates =
          newdates.getFullYear() +
          "-" +
          (newdates.getMonth() + 1) +
          "-" +
          (newdates.getDate() < 10
            ? "0" + newdates.getDate()
            : newdates.getDate());

        // let firstDate = new Date(Date.parse(new Date()));
        // firstDate.setDate(i);
        // let newdate = yeare + "-" + nowmonth + "-" + i;
        this.dateweeklist.push(vardates);
        let dayNum = {
          label: vardates,
          key: this.weekproplist[i],
          weekName: this.weekdaylist[i],
          fn: vardates,
        };
        this.formThead.push(dayNum);
      }
      //console.log(this.formThead);
      let endtime =
        newdates2.getMonth() +
        1 +
        "/" +
        (newdates2.getDate() < 10
          ? "0" + newdates2.getDate()
          : newdates2.getDate());
      this.week_time = stratime + "-" + endtime;
    },
  },
};
</script>

<style scoped>
.el-main {
  height: 100%;
  /* border: 1px solid red; */
  width: 100%;
}

.el-scrollbar {
  height: 100%;
  padding-top: 15px;
}
.el-container {
  height: 100%;
}
</style>

<template>
  <el-card class="page-container" style="padding: 0px;">
    <el-row>
      <el-col :span="24">
        <div style="margin:0 0 10px 0;">
          <el-radio-group v-model="YpjxcRc.orderby" size="mini" @change="orderbyChange">
            <el-radio-button label="batch_no">批号</el-radio-button>
            <el-radio-button label="code">商品</el-radio-button>

          </el-radio-group>
        </div>
      </el-col>
      <el-col :span="24">
        <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="查询时间" style="margin-right: 3px;">
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
          <el-form-item label="类型" style="margin-right: 3px;">
            <el-select v-model="formInline.lx" placeholder="类型">
              <el-option label="全部" value="qb"></el-option>
              <el-option label="西药" value="medicalType_0"></el-option>
              <el-option label="中草药" value="medicalType_1"></el-option>
              <el-option label="中成药" value="medicalType_2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态" style="margin-right: 3px;">
            <el-select v-model="formInline.zt" placeholder="状态">
              <el-option label="全部" value="qb"></el-option>
              <el-option label="停用" value="0"></el-option>
              <el-option label="启用" value="1"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="批号" style="margin-right: 3px;">
            <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
          </el-form-item>
          <el-form-item label="商品名" style="margin-right: 3px;">
            <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
          </el-form-item>

          <!-- <el-collapse-transition style="margin-right: 5px;">
            <div v-show="show3">
              <el-form-item label="批号" style="margin-right: 5px;">
        <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
      </el-form-item>
        <el-form-item label="商品名" style="float:left;margin-right: 5px;">
        <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
      </el-form-item>
            </div>
          </el-collapse-transition> -->

          <!-- <el-form-item label="批号">
            <el-input v-model="formInline.ph" placeholder="请输入批号"></el-input>
          </el-form-item>
            <el-form-item label="商品名">
            <el-input v-model="formInline.spm" placeholder="请输入商品名"></el-input>
          </el-form-item> -->
          <el-form-item style="margin-right: 3px;">
            <el-button type="primary" @click="onSubmit">查询</el-button>
            <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>
            <!-- <el-button @click="show3 = !show3">{{show3==true?"收起":"展开"}}</el-button> -->
          </el-form-item>
        </el-form>
      </el-col>
      <el-col :span="24">
        <el-table
          :data="tableData"
          ref="tableData"
          border
          height="calc(100vh - 330px)"
          :summary-method="getSummaries"
          show-summary
        >
          <el-table-column
            label="序号"
            type="index"
            :index="indexMethod"
            align="center">
          </el-table-column>
          <el-table-column
            align="center"
            prop="ypbm"
            label="药品编码">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="ypmc"
            label="药品名称">
          </el-table-column>
          <el-table-column
            width="200px"
            align="center"
            prop="fl"
            :formatter="stateFormat"
            label="分类">
          </el-table-column>
          <el-table-column
            width="120px"
            prop="ph"
            label="批号">
          </el-table-column>
          <el-table-column
            width="150px"
            prop="txm"
            label="条形码">
          </el-table-column>
          <el-table-column
            width="150px"
            prop="gyzz"
            label="国药准字">
          </el-table-column>
          <el-table-column
            prop="gg"
            width="150px"
            align="center"
            label="规格">
          </el-table-column>
          <el-table-column
            prop="sccj"
            label="生产厂家">
          </el-table-column>
          <el-table-column
            align="center"
            prop="gys"
            label="供应商">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="lsj"
            label="零售价">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="cbj"
            label="成本价">
          </el-table-column>

          <el-table-column
            width="200px"
            align="center"
            prop="yxq"
            label="有效期">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="qcsl"
            label="期初数量">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="qccb"
            label="期初成本">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqrk"
            label="本期入库数量">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqrkcb"
            label="本期入库成本">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqrkxse"
            label="本期入库销售额">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqcksl"
            label="本期出库数量">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqckcb"
            label="本期出库成本">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqckxse"
            label="本期出库销售额">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqpdsl"
            label="本期盘点数量">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqpdcb"
            label="本期盘点成本">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="bqpdxse"
            label="本期盘点销售额">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="qmsl"
            label="期末数量">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="qmcb"
            label="期末成本">
          </el-table-column>
          <el-table-column
            width="150px"
            align="center"
            prop="qmxse"
            label="期末销售额">
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
  </el-card>
</template>

<script>
import {getypjxcmanagementlist, getypjxcmanagementsums} from "@/api/toll/tollInfo";
import {BigNumber} from "bignumber.js";

export default {

  data() {
    return {
      show3: false,
      //时间选择
      TimeInterval: this.getInitializeDate(),
      formInline: {
        spm: '',
        zt: '',
        lx: '',
        ph: '',
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
      // tableData: [{
      //   ypbm:'',
      //   ypmc:'',
      //   txm:'',
      //   gyzz:'',
      //   gg:'',
      //   sccj:'',
      //   gys:'',
      //   lsj:'',
      //   cbj:'',
      //   ph:'',
      //   yxq:'',
      //   qcsl:'',
      //   qccb:'',
      //   bqrk:'',
      //   bqrkcb:'',
      //   bqcksl:'',
      //   bqckcb:'',
      //   qmsl:'',
      //   qmcb:'',
      // }],
      tableData: [],

      YpjxcRc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        kssj: "",
        jssj: "",
        lx: "",
        zt: "",
        spm: "",
        ph: "",
        orderby: "batch_no"
      },
      currentPage: 1,
      patientTotal: 0,
      patientList: [],
      allTotal: {},

    };
  },
  updated() {
    this.$nextTick(() => {
      this.$refs['tableData'].doLayout();
    })
  },
  methods: {
    stateFormat(row, column) {
      if (row.fl == "medicalType_0") {
        return '西药'
      } else if (row.fl == "medicalType_1") {
        return '中草药'
      } else if (row.fl == "medicalType_2") {
        return '中成药'
      } else {
        return ''
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
    getSummaries(param) {
      const {columns, data} = param;
      const sums = [];
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '总价';
          return;
        }
          // else if (index === 8 || index ===9 ||index >11){
          //   const values = data.map(item => Number(item[column.property]));
          // if (!values.every(value => isNaN(value))) {
          //   sums[index] = values.reduce((prev, curr) => {
          //     const value = Number(curr);
          //     if (!isNaN(value)) {
          //       return prev + curr;
          //     } else {
          //       return prev;
          //     }
          //   }, 0);
          //   sums[index] += '';
          // }

        // }
        else {
          sums[index] = '';
        }

      });
      // sums[10] = new BigNumber(Number(this.allTotal.lsj)).toFormat(2)+'元'
      // sums[11] = new BigNumber(Number(this.allTotal.cbj)).toFormat(2)+'元'
      // sums[13] = new BigNumber(Number(this.allTotal.qcsl)).toFormat(2)
      sums[14] = new BigNumber(Number(this.allTotal.qccb)).toFormat(2) + '元'
      // sums[15] = new BigNumber(Number(this.allTotal.bqrk)).toFormat(2)
      sums[16] = new BigNumber(Number(this.allTotal.bqrkcb)).toFormat(2) + '元'
      sums[17] = new BigNumber(Number(this.allTotal.bqrkxse)).toFormat(2) + '元'
      // sums[18] = new BigNumber(Number(this.allTotal.bqcksl)).toFormat(2)
      sums[19] = new BigNumber(Number(this.allTotal.bqckcb)).toFormat(2) + '元'
      sums[20] = new BigNumber(Number(this.allTotal.bqckxse)).toFormat(2) + '元'
      // sums[21] = new BigNumber(Number(this.allTotal.bqpdsl)).toFormat(2)
      sums[22] = new BigNumber(Number(this.allTotal.bqpdcb)).toFormat(2) + '元'
      sums[23] = new BigNumber(Number(this.allTotal.bqpdxse)).toFormat(2) + '元'
      // sums[24] = new BigNumber(Number(this.allTotal.qmsl)).toFormat(2)
      sums[25] = new BigNumber(Number(this.allTotal.qmcb)).toFormat(2) + '元'
      sums[26] = new BigNumber(Number(this.allTotal.qmxse)).toFormat(2) + '元'

      return sums;
    },
    onSizeChange(val) {
      this.currentPage = 1
      this.YpjxcRc.limit = val;
      this.YpjxcRc.offset = (this.currentPage - 1) * val
      this.Getypjxclist();
    },
    onCurrentChange(val) {
      this.YpjxcRc.offset = (val - 1) * this.YpjxcRc.limit
      this.currentPage = val
      this.Getypjxclist();
    },
    indexMethod(index) {
      return (this.currentPage - 1) * this.YpjxcRc.limit + index + 1;
    },
    resetCondition() {
      this.YpjxcRc = {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        kssj: "",
        jssj: "",
        lx: "",
        zt: "",
        spm: "",
        ph: "",
        orderby: "batch_no",
      },
        this.formInline = {
          spm: '',
          zt: '',
          lx: '',
          ph: '',
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
        this.YpjxcRc.kssj = this.TimeInterval[0];
        this.YpjxcRc.jssj = this.TimeInterval[1]
      }
      this.YpjxcRc.zt = this.formInline.zt == "qb" ? "" : this.formInline.zt;
      this.YpjxcRc.lx = this.formInline.lx == "qb" ? "" : this.formInline.lx;
      this.YpjxcRc.spm = this.formInline.spm;
      this.YpjxcRc.ph = this.formInline.ph;
      this.YpjxcRc.orderby = this.YpjxcRc.orderby;
      getypjxcmanagementlist(this.YpjxcRc).then((responseData) => {
        if (responseData.code == 100) {
          this.patientTotal = responseData.data.total;
          if (responseData.data.total > 0) {
            responseData.data.rows.forEach((item) => {
              let qcsl = Math.floor(item.qcsl / item.zj) >= 0 ? Math.floor(item.qcsl / item.zj) + (item.dw) + ((item.qcsl % item.zj > 0) ? (item.qcsl % item.zj) + item.zxdw : "") : item.sl + item.zxdw;
              let bqrk = Math.floor(item.bqrk / item.zj) >= 0 ? Math.floor(item.bqrk / item.zj) + (item.dw) + ((item.bqrk % item.zj > 0) ? (item.bqrk % item.zj) + item.zxdw : "") : item.bqrk + item.zxdw;
              let bqcksl = Math.floor(item.bqcksl / item.zj) >= 0 ? Math.floor(item.bqcksl / item.zj) + (item.dw) + ((item.bqcksl % item.zj > 0) ? (item.bqcksl % item.zj) + item.zxdw : "") : item.bqcksl + item.zxdw;
              let qmsl = Math.floor(item.qmsl / item.zj) >= 0 ? Math.floor(item.qmsl / item.zj) + (item.dw) + ((item.qmsl % item.zj > 0) ? (item.qmsl % item.zj) + item.zxdw : "") : item.qmsl + item.zxdw;
              let bqpdsl = Math.floor(item.bqpdsl / item.zj) >= 0 ? Math.floor(item.bqpdsl / item.zj) + (item.dw) + ((item.bqpdsl % item.zj > 0) ? (item.bqpdsl % item.zj) + item.zxdw : "") : item.bqpdsl + item.zxdw;

              this.tableData.push({
                ypbm: item.ypbm,
                ypmc: item.ypmc,
                txm: item.txm,
                gyzz: item.gyzz,
                gg: item.gg,
                sccj: item.sccj,
                gys: item.gys,
                lsj: item.lsj,
                cbj: item.cbj,
                ph: item.ph,
                yxq: item.yxq,
                // qcsl:item.qcsl, zj 制剂
                // qcsl: Math.floor(item.qcsl / item.zj) >= 0 ? Math.floor(item.qcsl / item.zj) + (item.dw) + ((item.qcsl % item.zj > 0) ? (item.qcsl % item.zj) + item.zxdw : "") : item.sl + item.zxdw,
                // qccb: item.qccb,
                // bqrk: Math.floor(item.bqrk / item.zj) >= 0 ? Math.floor(item.bqrk / item.zj) + (item.dw) + ((item.bqrk % item.zj > 0) ? (item.bqrk % item.zj) + item.zxdw : "") : item.bqrk + item.zxdw,
                // bqrkcb: item.bqrkcb,
                // bqcksl: Math.floor(item.bqcksl / item.zj) >= 0 ? Math.floor(item.bqcksl / item.zj) + (item.dw) + ((item.bqcksl % item.zj > 0) ? (item.bqcksl % item.zj) + item.zxdw : "") : item.bqcksl + item.zxdw,
                // bqckcb: item.bqckcb,
                // qmsl: Math.floor(item.qmsl / item.zj) >= 0 ? Math.floor(item.qmsl / item.zj) + (item.dw) + ((item.qmsl % item.zj > 0) ? (item.qmsl % item.zj) + item.zxdw : "") : item.qmsl + item.zxdw,
                // qmcb: item.qmcb,
                // bqpdsl: Math.floor(item.bqpdsl / item.zj) >= 0 ? Math.floor(item.bqpdsl / item.zj) + (item.dw) + ((item.bqpdsl % item.zj > 0) ? (item.bqpdsl % item.zj) + item.zxdw : "") : item.bqpdsl + item.zxdw,
                // bqpdcb: item.bqpdcb,

                qcsl: qcsl,
                qccb: item.cbj * item.qcsl,
                bqrk: bqrk,
                bqrkcb: item.bqrkcb,
                bqrkxse: item.bqrkxse,
                bqcksl: bqcksl,
                bqckcb: item.bqckcb,
                bqckxse: item.bqckxse,
                bqpdsl: bqpdsl,
                bqpdcb: item.bqpdcb,
                bqpdxse: item.bqpdxse,
                qmsl: qmsl,
                qmcb: item.qmcb,
                qmxse: item.qmxse,
                fl: item.fl,
              })
            })
          }
          getypjxcmanagementsums(this.YpjxcRc).then((ref) => {
            if (ref.data.total > 0) {
              this.allTotal = ref.data.rows[0];
            }
          })
        }

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



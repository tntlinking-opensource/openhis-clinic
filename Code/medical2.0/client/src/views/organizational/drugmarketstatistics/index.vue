<template>
  <el-row v-loading="loading">
    <el-card class="page-container">
      <el-row>
        <el-col style="text-align:right;margin-bottom:10px">
          <el-button type="primary" icon="el-icon-upload2" @click='exportExcel'>导出</el-button>
        </el-col>
      </el-row>
      <!--  搜索栏  开始 -->
      <div class='query-form-container'>
        <el-row class='search-row'>
          <el-form :model='queryModel' @submit.native.prevent label-position="left" label-width='70px' ref='queryForm'
                   :inline-message='true'>
            <el-col :span="8">
              <el-form-item label='收费日期' prop='time'>
                <el-date-picker
                  v-model="queryModel.dateRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  :default-time="['00:00:00', '23:59:59']"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  format="yyyy-MM-dd HH:mm:ss"
                >
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label="诊所" style="margin-right: 3px;">
                <el-select v-model="queryModel.jgid" placeholder="诊所">
                  <el-option label="全部" value="qb"></el-option>
                  <el-option v-for="item in jglist" :value="item.jgid" :key="item.jgid" :label="item.jgmc">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='药品名称' prop='name'>
                <el-input
                  style="width:320px;"
                  v-model="queryModel.name"
                  placeholder="请输入药品名称或者拼音码"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item label='药品类型' prop='type'>
                <el-select
                  v-model="queryModel.type"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请输入药品名称或拼音码"
                  class="typeClass"
                >
                  <el-option
                    v-for="type in type_List"
                    :key="type.value"
                    :label="type.name"
                    :value="type"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-form>
        </el-row>

        <el-row id="conditionOperation">
          <el-col :span="24" style="text-align:right;padding-right:5px">
            <el-button type="primary" icon="el-icon-search" @click='onSearch' :plain='true'>搜索</el-button>
            <el-button type="info" icon="el-icon-refresh-left" @click='resetCondition' :plain='true'>重置</el-button>


          </el-col>
        </el-row>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="dispensing_table"
              :data="dispensingList"
              height="calc(100vh - 284px)"
              border
              ref="mutipleTable1"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              show-summary
              :summary-method='getTotal'

            >
              <!-- show-summary -->
              <!-- :summary-method='getTotal' -->
              <el-table-column
                label="序号"
                type="index"
                :index="indexMethod"
                align="center">
              </el-table-column>
              <el-table-column
                align="center"
                prop="jgid"
                sortable v-if="false"
                label="诊所编码">
              </el-table-column>
              <el-table-column
                align="center"
                prop="jgmc"
                label="诊所名称">
              </el-table-column>
              <el-table-column prop="type" label="药品分类">
              </el-table-column>
              <el-table-column prop="name" label="药品名称"></el-table-column>
              <el-table-column prop="dosis" label="规格">
                <template slot-scope="scope">
                  <template>
                    {{ scope.row.dosis }}{{ scope.row.dosisUnit }}*{{ scope.row.preparation }}{{ scope.row.preparationUnit }}/{{ scope.row.pack }}
                  </template>
                </template>
              </el-table-column>
              <el-table-column prop="total" label="数量">
                <template slot-scope="scope">
                  {{ Math.floor(scope.row.total / scope.row.preparation) >= 0 ? Math.floor(scope.row.total / scope.row.preparation) + (scope.row.pack) + ((scope.row.total % scope.row.preparation > 0) ? (scope.row.total % scope.row.preparation) + scope.row.preparationUnit : "") : scope.row.total + scope.row.preparationUnit }}
                </template>
              </el-table-column>
              <el-table-column prop="allFee" label="总价">
                <template slot-scope="scope">
                  <!-- <span style="display:inline-block;width:100%;text-align:right"> -->
                  {{ bigNum(scope.row.allFee ? scope.row.allFee : 0) }}元
                  <!-- </span> -->
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
      <!-- 表格栏  结束 -->
      <!-- 分页栏     开始 -->
      <el-row>
        <el-col :span="24">
          <el-pagination
            background
            @size-change="onSizeChange"
            @current-change="onCurrentChange"
            :current-page.sync="currentPage"
            :page-sizes="[20, 50, 100, dispensingTotal]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="dispensingTotal"
          >
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>
  </el-row>
</template>

<script>
import reportViewMixin from '@/mixins/reportViewMixin'
import { BigNumber } from "bignumber.js";
import { validatenull } from "@/utils/validate";
import { getdrugmarketstatistics, getdrugmarketstatisticsStat, exportExcel } from "@/api/toll/drugDetail";
import { getjglist } from "@/api/toll/tollInfo";

export default {
  mixins: [reportViewMixin],
  data() {
    return {
      listApi: getdrugmarketstatistics,
      statApi: getdrugmarketstatisticsStat,
      exportApi: exportExcel,
      entityName: 'DrugMarketStatistics',
      permissionPrefix: 'drugmarketstatistics',
      exportColumnName: 'drugDetail',
      queryModel: {
        name: '',
        type: '',
        dateRange: [this.addCreateDate(), new Date()],
        jgid: null,
      },
      jglist: [],
      YpjxcRc: {
        limit: 20,
        offset: 0,
        companyId: currentUser.company.id,
        orderby: "batch_no",
        jgid: null,
      },
      zsids: [],
    };
  },
  updated() {
    this.$nextTick(() => {
      this.$refs.mutipleTable1.doLayout();
    });
  },
  methods: {
    exportExcel() {
      this.search.columnName = this.exportColumnName
      if (!this.exportApi) return
      this.exportApi(this.search).then((res) => {
        const filename = decodeURI(res.headers.split(';')[1].split('=')[1]) || '.xls'
        const blob = new Blob([res.data], { type: 'application/octet-stream' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('download', filename)
        document.body.appendChild(link)
        link.click()
      }).catch((error) => {
        this.outputError(error)
      })
    },

    getTotal(param) {
      let { columns } = param;
      let arr = []
      columns.forEach((column, index) => {
        if (index === 0) {
          arr[index] = '合计';
        } else {
          arr[index] = ''
        }
      })
      let allFee = this.allTotal != null ? this.allTotal.allFee : 0
      arr[6] = new BigNumber(Number(allFee)).toFormat(2) + '元'
      return arr
    },

    appendSearchParams() {
      // 诊所过滤
      if (this.queryModel.jgid != null && this.queryModel.jgid !== 'qb') {
        this.search.params.push({
          columnName: " company_id",
          queryType: "=",
          value: this.queryModel.jgid,
        })
      } else {
        let jgidlists = [];
        this.jglist.forEach((item) => {
          jgidlists.push(item.jgid)
        })
        if (jgidlists.length > 0) {
          this.search.params.push({
            columnName: " oc.id",
            queryType: "in",
            value: jgidlists,
          })
        }
      }
      // 日期范围
      if (this.queryModel.dateRange && this.queryModel.dateRange.length) {
        this.queryModel.dateRange[0] = this.$moment(this.queryModel.dateRange[0]).format("YYYY-MM-DD HH:mm:ss")
        this.queryModel.dateRange[1] = this.$moment(this.queryModel.dateRange[1]).format("YYYY-MM-DD HH:mm:ss")
        this.search.params.push(
          { logic: "AND", queryType: "(" },
          { columnName: "create_date", logic: "", queryType: 'between', value: this.queryModel.dateRange },
          { logic: "", queryType: ")" }
        )
      }
      // 药品类型
      this.search.params.push({
        columnName: "d.type",
        queryType: "=",
        value: validatenull(this.queryModel.type.value) ? "" : this.queryModel.type.value,
      })
      // 药品名称
      if (this.queryModel.name !== undefined && this.queryModel.name !== '') {
        this.search.params.push({
          columnName: "d.goods_name",
          queryType: "like",
          value: this.queryModel.name,
        })
      }
    },

    loadData() {
      this.setLoad()
      this.search.params = []
      this.appendSearchParams()
      this.listApi(this.search).then(responseData => {
        if (responseData.code === 100) {
          this.dispensingList = responseData.data.rows
          this.dispensingTotal = responseData.data.total
          if (this.statApi) {
            this.statApi(this.search).then((res) => {
              if (res.code === 100) {
                this.allTotal = res.data
                this.resetLoad()
              }
            }).catch(() => {})
          } else {
            this.resetLoad()
          }
        } else {
          this.showMessage(responseData)
          this.resetLoad()
        }
      }).catch(error => {
        this.outputError(error)
      })
    },

    resetCondition() {
      this.queryModel = {
        name: '',
        type: '',
        jgid: null,
        dateRange: [this.addCreateDate(), new Date()]
      }
      this.currentPage = 1
      this.onSearch()
    },

    Getcliniclist() {
      this.jglist = [];
      this.zsids = [];
      getjglist(this.YpjxcRc).then((responseData) => {
        if (responseData.code === 100) {
          if (responseData.data.length > 0) {
            responseData.data.forEach((item) => {
              if (item.jgid !== currentUser.company.id) {
                this.jglist.push({
                  jgid: item.jgid,
                  jgmc: item.jgmc,
                })
                this.zsids.push({ jgid: item.jgid })
              }
            })
          }
        }
      })
    },
  },
  watch: {},
  mounted() {
    this.initOptions()
    this.Getcliniclist()
    this.pageInit()
  },
};
</script>
<style lang="scss" scoped>
  .page-container {
    padding: 0;
  }

  .query-form-container {
    padding: 0px 0px 10px 0px;
  }

  .typeClass {
    ::v-deep .el-input {
      width: 100% !important;

      input {
        width: 100% !important;
        padding-right: 0;
      }
    }
  }

  .el-col {
    ::v-deep .el-range-separator {
      width: 10%;
    }
  }

  .cell {
    span {
      display: inline-block;
      width: 100%;
      text-align: right;
    }
  }

  ::v-deep .el-table__footer-wrapper {
    td:not(:nth-of-type(1)) {
      .cell {
        display: inline-block;
        width: 100%;
        text-align: left;
      }
    }

  }

  .el-table::before {
    height: 0;
  }

  ::v-deep .el-table colgroup col[name='gutter'] {
    width: 5px !important
  }

  ::v-deep .el-table__body {
    width: 100% !important
  }
</style>

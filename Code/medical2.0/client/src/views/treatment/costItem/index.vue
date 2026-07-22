<template>
  <el-row v-loading="loading">
    <!-- 历史记录  -->
    <History :bussObject="curentRow"></History>
    <!-- 编辑窗口  -->
    <costItem-form
      ref="costItemForm"
      :permission="permission"
      v-on:save-finished="loadData"
    ></costItem-form>
    <el-card class="page-container">
      <!--  搜索栏  开始 -->
      <div class="query-form-container">
        <el-row v-if="!moreCodition" class="search-row">
          <el-form
            :model="queryModel"
            @submit.native.prevent
            label-position="left"
            label-width="70px"
            ref="queryForm"
            :inline-message="true"
          >
            <el-col :span="6">
              <el-form-item label="项目名称" prop="itemName">
                <el-input
                  v-model="queryModel.itemName"
                  :clearable="true"
                  placeholder="请输入项目名称"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="项目类别" prop="itemType">
                <el-select
                  v-model="queryModel.itemType"
                  value-key="value"
                  filterable
                  clearable
                  placeholder="请选择项目类别"
                >
                  <el-option
                    v-for="itemType in itemType_List"
                    :key="itemType.value"
                    :label="itemType.name"
                    :value="itemType"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="6">
              {{ queryModel.isUse1 }}
              <el-form-item label="启用状态" prop="isUse">
                <el-switch
                  v-model="queryModel.isUse"
                  active-color="#13ce66"
                  inactive-color="#dbdfe6"
                  active-value="1"
                  inactive-value="0"
                ></el-switch>
              </el-form-item>
            </el-col> -->
            <el-col :span="5">
              <el-form-item label='启用状态' prop='isUse'>
                  <el-select v-model="queryModel.isUse" placeholder="请选择是否启用">
                    <el-option
                      v-for="item in isUse"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="3" style="display:flex;justivy-content:space-around">
               <el-button
                    type="primary"
                    icon="el-icon-search"
                    @click="onSearch()"
                    :plain="true"
                  >搜索</el-button>
                <el-button
                    type="info"
                    icon="el-icon-refresh-left"
                    @click="reset"
                    :plain="true"
                  >重置</el-button>
            </el-col>
            <el-col :span="4" style="text-align:right;padding-right:5px">
              <el-button-group>
                  <el-button
                    v-show="permission.add"
                    type="primary"
                    icon="el-icon-plus"                   
                    @click="onCreateCostItem()"
                    >添加</el-button
                  >
              </el-button-group>
            </el-col>
          </el-form>
        </el-row>
        <QueryForm
          v-else
          v-model="moreParm"
          :tableId="tableId"
          :schemeId="schemeId"
          :routerId="$route.meta.routerId"
          @search="onSearch()"
          @moreCodition="onMoreCodition()"
        ></QueryForm>
      </div>
      <!--  搜索栏  结束 -->

      <!-- 工具栏 开始 -->
      
      <!-- 工具栏 结束 -->

      <!-- 表格栏  开始 -->
      <el-row>
        <el-col :span="24">
          <div @mouseleave="moveTableOutside">
            <el-table
              class="drag_table"
              :data="costItemList"
              border
              :height="tableHeight"
              @sort-change="onSortChange"
              @header-dragend="onChangeWidth"
              :cell-class-name="cellClassName"
              :header-cell-class-name="headerCellClassName"
              highlight-current-row
              ref="tableRef" 
            >
            <el-table-column
              label="序号"
              type="index"
              :index="indexMethod"
              align="center">
            </el-table-column>
              <el-table-column
                v-for="(cv, index) in columnViews"
                v-if="cv.display"
                :prop="cv.prop"
                :key="`columnViews_${index}`"
                :label="cv.label"
                sortable="custom"
                :align="cv.align"
                :min-width="cv.miniWidth + 'px'"
                :width="cv.width + 'px'"
                header-align="center"
                :column-key="index.toString()"
                :render-header="renderHeader"
              >
                <template slot-scope="{ row, $index }">
                  <span
                    v-if="
                      columnViews[index].showType === 'Switch' ||
                      columnViews[index].showType === 'Checkbox' ||
                      columnViews[index].showType === 'Radio'
                    "
                  >
                    <li
                      v-if="getAttrValue(row, columnViews[index].prop) === '1'"
                      class="el-icon-check"
                      style="color: #f56c6c"
                    ></li>
                  </span>
                  <span v-else>{{
                    getAttrValue(
                      row,
                      columnViews[index].prop,
                      columnViews[index].javaType
                    )
                  }}</span>
                </template>
              </el-table-column>
              <!--表行级操作按钮-->
              <el-table-column
                label="操作"
                header-align="center"
                :width="130 + 'px'"
                :key="'operate'"
              >
                <template slot="header" slot-scope="scope">
                  <span>操作</span>
                  <view-columns-select
                    v-model="columnViews"
                    v-on:save-column-view="saveColumn"
                    v-on:show-all-column="showAllColumn"
                    v-on:show-default-column="showDefaultColumn"
                  ></view-columns-select>
                  <export-excel-button
                    v-show="permission.export"
                    :data="costItemList"
                    :tHeader="getHeads()"
                    :filterVal="getFilterVal()"
                    :plain="true"
                  ></export-excel-button>
                </template>
                <template slot-scope="scope">
                  <OperationIcon
                    v-show="permission.view"
                    type="info"
                    content="查看"
                    placement="top-start"
                    icon-name="el-icon-view"
                    @click="onViewEntity(scope.$index, scope.row, 'costItemForm')"
                  ></OperationIcon>
                  <OperationIcon
                    v-show="permission.edit"
                    type="primary"
                    content="编辑"
                    placement="top-start"
                    icon-name="el-icon-edit"
                    @click="onEditCostItem(scope.$index, scope.row)"
                  ></OperationIcon>
                  <OperationIcon
                    v-show="permission.add"
                    type="primary"
                    content="复制"
                    placement="top-start"
                    icon-name="el-icon-document"
                    @click="onCopyCostItem(scope.$index, scope.row)"
                  ></OperationIcon>
                  <!-- <OperationIcon
                    v-show="permission.remove"
                    type="danger"
                    content="删除"
                    placement="top-start"
                    icon-name="el-icon-delete"
                    @click="onDeleteEntity(scope.$index, scope.row, deleteApi)"
                  ></OperationIcon> -->
                  <OperationIcon
                    v-show="permission.view"
                    type="info"
                    content="历史记录"
                    placement="top-start"
                    icon-name="el-icon-info"
                    @click="onShowHistory(scope.$index, scope.row)"
                  ></OperationIcon>
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
            :page-sizes="[20, 50, 100, costItemTotal]"
            :page-size="20"
            layout="total, sizes, prev, pager, next, jumper"
            :total="costItemTotal"
          >
          </el-pagination>
        </el-col>
      </el-row>
      <!-- 分页栏     结束 -->
    </el-card>
  </el-row>
</template>

<script>
import { validatenull } from "@/utils/validate";
import {
  listCostItemPage,
  getCostItemById,
  deleteCostItem,
} from "@/api/treatment/costItem";
import listViewMixin from "@/mixins/listViewMixin";
import CostItemForm from "./costItemForm";
import { listDictItemAll } from "@/api/sys/dictItem";
import { getDictItemsByCode, DICT_CODE } from '@/utils/dictCache'
import { listCostItemAll } from "@/api/treatment/costItem";
import ExportExcelButton from "@/components/ExportExcelButton";
import ViewColumnsSelect from "@/views/components/ViewColumnsSelect";
import QueryForm from "@/views/components/queryForm";
import MainUI from "@/views/components/mainUI";
import OperationIcon from "@/components/OperationIcon";
import History from "@/views/components/history";
import { getCurrentUser, getCurrentCompanyId } from "@/utils/userCache";
export default {
  extends: MainUI,
  mixins: [listViewMixin],
  components: {
    CostItemForm,
    ExportExcelButton,
    ViewColumnsSelect,
    QueryForm,
    OperationIcon,
    History,
  },
  
  data() {
    
    return {
      listApi: listCostItemPage,
      getApi: getCostItemById,
      deleteApi: deleteCostItem,
      entityName: 'CostItem',
      permissionPrefix: 'costItem',
      costItemDTO:{
        response:{},
        res:{}
      },//修改操作对象封装
      queryTypes: {
        item_name: "like",
        item_type: "=",
        is_use: "=",
      },
      queryModel: {
        itemName: "", // 项目名称
        itemType: {
          // 项目类别
          value: "",
          name: "",
        },
        isUse: "", // 状态
      },
      tableHeight: "",//表格高度
       isUse:[
            {
              value: '0',
              label: '否'
            }, 
            {
              value: '1',
              label: '是'
            },
          ],//是否启用
      subproject:[],//子项目封装
      costltemSearch: {
        params: [{ columnName: "company_id", queryType: "=", value: "" }],
        offset: 0,
        limit: 100,
        columnName: "", // 排序字段名
        order: "", // 排序
      },
      costItemTotal: 0,
      costItemList: [],

      itemType_List: [], // 项目类别

      oprColumnWidth: 140, // 操作列宽
      tableId: "998465736089977637",
      schemeId: "998465736089977654",
    };
  },
  methods: {

     //计算table高度(动态设置table高度)
    getTableHeight() {
      let tableH = 240; //距离页面下方的高度
      let tableHeightDetil = window.innerHeight - tableH;
      if (tableHeightDetil <= 300) {
        this.tableHeight = 300;
      } else {
        this.tableHeight = window.innerHeight - tableH;
      }
    },


    appendSearchParams() {
      this.search.params.push({
        columnName: "company_id",
        queryType: "=",
        value: getCurrentCompanyId(),
      });
      if (this.moreCodition) {
        this.search.params = this.search.params.concat(
          this.compositeCondition()
        );
      } else {
        this.search.params.push({
          columnName: "item_name",
          queryType: "like",
          value: this.queryModel.itemName,
        });
        if (this.queryModel.itemType.value !== "") {
          this.search.params.push({
            columnName: "item_type",
            queryType: "=",
            value: validatenull(this.queryModel.itemType.value)
              ? ""
              : this.queryModel.itemType.value,
          });
        }
        this.search.params.push({
          columnName: "is_use",
          queryType: "=",
          value: this.queryModel.isUse,
        });
      }
      this.pushDataPermissions(
        this.search.params,
        this.$route.meta.routerId,
        this.tableId
      );
    },
    handleListResponse(responseData) {
      this.costItemTotal = responseData.data.total;
      this.costItemList = responseData.data.rows;
    },
    onCreateCostItem() {
      //  this.costltemSearch.params = [
      //   {
      //     columnName: "company_id",
      //     queryType: "=",
      //     value: this.company_id,
      //   },
      // ];
      this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: getCurrentCompanyId(),
          },
        ];
      listCostItemAll(this.search).then((res)=>{
        if(res.code===100){
         // this.subproject=res.data
          this.$refs.costItemForm.openAddCostItemDialog(res.data);
        }
      }).catch(()=>{})

    },
    onEditCostItem(index, row) {
      this.setLoad();
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code === 100) {

             this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: getCurrentCompanyId(),
          },
        ];
      listCostItemAll(this.search).then((res)=>{
        if(res.code===100){
         // this.subproject=res.data
         this.costItemDTO.response=responseData.data
         this.costItemDTO.res=res.data
       
          this.$refs.costItemForm.openEditCostItemDialog(this.costItemDTO);
        }
      }).catch(()=>{})
           
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    onCopyCostItem(index, row) {
      this.setLoad();
      // getCostItemById(row.id)
      //   .then((responseData) => {
      //     if (responseData.code == 100) {
      //       this.$refs.costItemForm.$emit(
      //         "openCopyCostItemDialog",
      //         responseData.data
      //       );
      //     } else {
      //       this.showMessage(responseData);
      //     }
      //     this.resetLoad();
      //   })
      //   .catch((error) => {
      //     this.outputError(error);
      //   });
      getCostItemById(row.id)
        .then((responseData) => {
          if (responseData.code === 100) {

             this.search.params = [
          {
            columnName: "company_id",
            queryType: "=",
            value: getCurrentCompanyId(),
          },
        ];
      listCostItemAll(this.search).then((res)=>{
        if(res.code===100){
         // this.subproject=res.data
         this.costItemDTO.response=responseData.data
         this.costItemDTO.res=res.data

          this.$refs.costItemForm.openCopyCostItemDialog(this.costItemDTO);
        }
      }).catch(()=>{})
           
          } else {
            this.showMessage(responseData);
          }
          this.resetLoad();
        })
        .catch((error) => {
          this.outputError(error);
        });
    },
    initOptions(This) {
      getDictItemsByCode(DICT_CODE.TREATMENT_ITEM_TYPE).then((data) => {
        this.itemType_List = data;
      });
    },
  },
  watch: {
    // tableData是el-table绑定的数据
      tableData: {
          // 解决表格显示错位问题
          handler () {
              this.$nextTick(() => {
                  // tableRef是el-table绑定的ref属性值
                  this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
              })
          },
          deep: true
      }
  },
  updated(){
    if(this.$refs.tableRef){
      this.$nextTick(() => {
          // tableRef是el-table绑定的ref属性值
          this.$refs.tableRef.doLayout()// 对 Table 进行重新布局
      })
    }
  },
  mounted() {
    this.pageInit();
     //挂载window.onresize事件(动态设置table高度)
    this._resizeHandler = () => {
      if (this.resizeFlag) {
        clearTimeout(this.resizeFlag);
      }
      this.resizeFlag = setTimeout(() => {
        this.getTableHeight();
        this.resizeFlag = null;
      }, 100);
    };
    window.addEventListener('resize', this._resizeHandler);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this._resizeHandler);
    if (this.resizeFlag) {
      clearTimeout(this.resizeFlag);
    }
  },
  created(){
     this.getTableHeight();
  }
};
</script>
<style lang="scss" scoped>
 .page-container{
   padding: 0;
 }
  
  .drag_table {
 // 设置表格header的高度
 ::v-deep th {
   height: 44px;
 }
::v-deep th.gutter:last-of-type {
  height: 0 !important;
}
 // 设置表格body的高度
 ::v-deep.el-table__body-wrapper {
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
::v-deep .el-table__body-wrapper{
    height: calc(100% - 44px) !important;
  }
</style>

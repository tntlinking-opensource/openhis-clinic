<template>
	<el-popover placement="right" v-if="!IsOnlyRead" width="600" trigger="click" popper-class="popover">

		<el-table :data="newList" border highlight-current-row @row-click="RowClickSurchargeTable">
			<el-table-column prop="drugStuffName" :label="nameTitle">
			</el-table-column>
			<el-table-column prop="drug.type.name" :label="药品类型" v-if="type==1">
			</el-table-column>
			<el-table-column prop="stuff.type.name" :label="材料类型" v-if="type==2">
			</el-table-column>
			<el-table-column prop="drug.price" label="零售" width="100" v-if="addType == 1">
			</el-table-column>
			<el-table-column prop="drug.pack.name" label="单位" width="80" v-if="addType == 1">
			</el-table-column>
			<el-table-column prop="supplierStock.batchNo" label="批号" width="80">
			</el-table-column>
			<el-table-column prop="supplierStock.norms" label="规格" width="80">
			</el-table-column>
			<el-table-column prop="occupyStock" label="占用库存" width="80">
			</el-table-column>
			<el-table-column prop="surplusStock" label="可用库存" width="80">
			</el-table-column>
		</el-table>
		<el-input  slot="reference" v-model="searchName" :placeholder="drugStuffPlaceholder">
		</el-input>

	</el-popover>
</template>

<script>
	// 药品、材料动态库存选择

	import {
		listPage
	} from "@/api/stock/medicinalStorageControl";
	import BaseUI from "@/views/components/baseUI";
	export default {
		extends: BaseUI,
		props: {
			supplierId: {
				type: String,
				default: null
			},
			type: {
				type: String,
				default: '1'
			}
		},

		data() {
			return {
				newList: [],
				queryList: [],
				searchName: null
			};
		},
		computed: {
			nameTitle() {
				return this.type == '1' ? '药品名称' : '材料名称';
			}
		},
		watch: {
			supplierId() {
        console.log('supplier change:',this.supplierId);
        this.queryList=[];
        this.newList=[];
				this.loadMedicinalStorageList();
			},
			searchName(newVal, oldVal) {
				this.filterList();
			}
		},
		methods: {
			loadMedicinalStorageList() {

				let searchParams = {
					params: [],
					limit: 1000,
					offset: 0,
					order: "",
					columnName: ""
				};
				searchParams.params.push({
					columnName: "company_id",
					queryType: "=",
					value: currentUser.company.id,
				}, {
					columnName: "type",
					queryType: "=",
					value: this.type,
				}, {
					columnName: "surplus_stock",
					queryType: ">",
					value: 0,
				}, {
					columnName: "supplierStock.supplier_id",
					queryType: "=",
					value: this.supplierId,
				});
				listPage(searchParams).then(res => {
					//console.log('storageListAll',res);
					this.queryList = res.data;
					this.filterList();
				}).catch((e) => {
					console.log('storageListAll error:', e);
				})
			},
			filterList() {
				let newVal = this.searchName;
				var pattern = new RegExp("[A-Za-z]+");
				if (!newVal) {
					this.newList = this.queryList;
				} else if (pattern.test(newVal)) {
					window.setTimeout(() => {
						this.newList = this.queryList.filter((i) => {
							if (i.drug && i.drug.id && i.drug.pinyinCode) {
								let newSearchName = this.searchName.toUpperCase();
								return i.drug.pinyinCode.indexOf(newSearchName) > -1;
							} else if (i.stuff && i.stuff.id && i.stuff.pinyinCode) {
								let newSearchName = this.searchName.toUpperCase();
								return i.stuff.pinyinCode.indexOf(newSearchName) > -1;
							}
							return false;
						});
					}, 1000);
				} else {
					window.setTimeout(() => {
						this.newList = this.queryList.filter((i) => {
							if (i.drugStuffName) {
								return i.drugStuffName.indexOf(this.searchName) > -1;
							}
						});
					}, 1000);
				}
			},
			RowClickSurchargeTable(row) {
				this.searchName = row.goodsName ? row.goodsName : row.name;
				/* var newStorage = {
				  goodsID: "",
				  goodsName: "",
				  specification: "",
				  factoryName: "",
				  factoryID: "",
				  batchNumber: "",
				  producedDate: null,
				  expireDate: null,
				  quantity: "",
				  inPrice: "",
				  outPrice: "",
				  totalInPrice: "",
				  totalOutPrice: "",
				  pack: {},
				  preparationUnit: {},
				  preparation: 0,
				  packNumber: 0,
				};
				newStorage.goodsID = row.id;
				newStorage.goodsName = row.goodsName ? row.goodsName : row.name;
				newStorage.goodsType = row.goodsType;
				newStorage.specification = row.specification;
				newStorage.factoryName = row.factory.name;
				newStorage.factoryID = row.factory.id;
				newStorage.outPrice = row.price;
				newStorage.pack = row.pack;
				newStorage.preparationUnit = row.preparationUnit
				newStorage.preparation = row.preparation - 0;
				newStorage.packNumber = row.packNumber;
				this.drugPreparation = row.preparation - 0;
				if (row.packNumber != undefined) {
				  this.stuffPackNum = row.packNumber;
				} else {
				  this.stuffPackNum = 0
				}

				// console.log(this.selectGoodsTemp, "0000333");
				this.selectGoodsTemp = newStorage; */
				this.$emit('onMedicinalStorageSelect', row);
			},
			getCurrentMedicinalStorage() {

			}
		},
		mounted() {
			this.loadMedicinalStorageList();
		},
	};
</script>

<style>
</style>

<template>
  <div v-loading="dashboardLoading" class="dashboard-box">
    <component
      v-if="existDataView"
      ref="embedded"
      :reportType="1"
      :is="reportView"
      :dashboardId="dashboardId"
      @hook:mounted="onloadDone"
      @EmbendChartClick='onEmbendChartClick'></component>
  </div>
</template>
<script>
  import Base from './Base'
  export default {
    extends: Base,
    name: 'Embedded',
    props: {
      dashboardId: {
        type: String,
        required: true
      }
    },
    data() {
      return {
        dashboardLoading: true
      }
    },
    mounted() {
      this.$nextTick(() => {
        this.$on('addViewFilter', function({componentId, fieldId, value, operator}) {
          const param = {
            component: {
              id: componentId,
              options: {
                attrs: {
                  fieldId: fieldId,
                  viewIds: []
                }
              }
            },
            value: Array.isArray(value) ? value : [value],
            operator: operator
          }
          this.addFilter(param)
        })

        this.$on('removeViewFilter', function(componentId) {
          this.removeFilter(componentId)
        })
      })
    },
    methods: {
      onEmbendChartClick(param) {
        this.$emit('EmbendChartClick', param)
      },
      onloadDone() {
        this.dashboardLoading = false
      },
      addFilter(param) {
        if(window["vue-dataviewer"] && this.$refs.embedded) {
          this.$refs.embedded.$emit("addEmbeddedViewFilter", param)
        } else {
          setTimeout(() => { this.addFilter(param) }, 50)
        }
      },
      removeFilter(id) {
        if(window["vue-dataviewer"] && this.$refs.embedded) {
          this.$refs.embedded.$emit("removeEmbeddedViewFilter", id)
        } else {
          setTimeout(() => { this.removeFilter(id) }, 50)
        }
      }
    }
  }
</script>
<style scoped lang="scss">
  .dashboard-box {
    width: 100%;
    background-size: 100% 100%!important;
    min-width: 200px;
    min-height: 300px;
    overflow-x: hidden;
  }
</style>

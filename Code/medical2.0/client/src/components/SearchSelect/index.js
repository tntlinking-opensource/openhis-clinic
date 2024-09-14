import SearchSelect from './SearchSelect'

const index = (Vue, opts = {}) => {
  Vue.component('SearchSelect', SearchSelect)
}

export default {
  install: index
}

import NumberInput from './NumberInput'

const index = (Vue, opts = {}) => {
  Vue.component('NumberInput', NumberInput)
}

export default {
  install: index
}

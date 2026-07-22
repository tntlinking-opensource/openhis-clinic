import { createCrudApi } from '@/utils/apiFactory'

const baseApi = createCrudApi('/stock/medicinalStorageControl')

export const listPage = baseApi.listAll

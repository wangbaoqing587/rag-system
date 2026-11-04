import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 60000,
})

request.interceptors.response.use(
  (res) => res,
  (err) => {
    const msg =
      err.response?.data?.message ||
      err.message ||
      '网络异常'
    return Promise.reject(new Error(msg))
  }
)

export default request

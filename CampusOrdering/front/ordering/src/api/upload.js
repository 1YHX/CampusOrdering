import request from '@/utils/request'

// 图片上传
export function uploadImage(formData) {
  return request({
    url: '/api/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 
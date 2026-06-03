import compression from 'vite-plugin-compression'

export default function createCompression(env) {
  // 从环境变量中获取压缩配置
  const { VITE_BUILD_COMPRESS } = env
  const plugin = []
  if (VITE_BUILD_COMPRESS) {
    // 获取所有的压缩配置
    const compressList = VITE_BUILD_COMPRESS.split(',')
    if (compressList.includes('gzip')) {
      // 使用gzip解压缩静态文件
      plugin.push(
        compression({
          // 压缩文件后缀
          ext: '.gz',
          // 是否删除原始文件，false表示不删除
          deleteOriginFile: false
        })
      )
    }
    if (compressList.includes('brotli')) {
      // 使用brotli解压缩静态文件
      plugin.push(
        compression({
          // 压缩文件后缀
          ext: '.br',
          // 压缩算法
          algorithm: 'brotliCompress',
          // 是否删除原始文件，false表示不删除
          deleteOriginFile: false
        })
      )
    }
  }
  return plugin
}
// 用来管理所有插件
import vue from'@vitejs/plugin-vue'
import createAutoImport from './auto-import'
import createSvgIcon from './svg-icon'
import createCompression from './compression'
import createSetupExtend from './setup-extend'
export default function createVitePlugins(viteEnv,isBuild=false){
    const vitePlugins=[vue()]
    vitePlugins.push(createAutoImport())
    vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isBuild))
    //生产环境才需要压缩插件
    isBuild&&vitePlugins.push(...createCompression(viteEnv))
    return vitePlugins
}
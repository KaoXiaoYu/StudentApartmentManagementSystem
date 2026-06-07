<template>
  <div class="dorm-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchForm.keyword" 
        placeholder="请输入寝室号或楼栋" 
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><Search /></el-icon>
          </el-button>
        </template>
      </el-input>
      
      <el-select 
        v-model="searchForm.building" 
        placeholder="楼栋" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="1号楼" value="1号楼" />
        <el-option label="2号楼" value="2号楼" />
        <el-option label="3号楼" value="3号楼" />
        <el-option label="4号楼" value="4号楼" />
      </el-select>
      
      <el-select 
        v-model="searchForm.gender" 
        placeholder="性别" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="男生" value="男" />
        <el-option label="女生" value="女" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        添加寝室
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="dormList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'success'">
              {{ scope.row.gender === '男' ? '男生' : '女生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="currentCount" label="当前人数" width="100" />
        <el-table-column prop="roomType" label="房型" width="100" />
        <el-table-column prop="facilities" label="设施" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '可用' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
    
    <!-- 添加/编辑弹窗 -->
    <el-dialog 
      :title="isEdit ? '编辑寝室' : '添加寝室'" 
      :visible.sync="showAddModal"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="楼栋" prop="building">
          <el-select v-model="form.building" placeholder="请选择楼栋">
            <el-option label="1号楼" value="1号楼" />
            <el-option label="2号楼" value="2号楼" />
            <el-option label="3号楼" value="3号楼" />
            <el-option label="4号楼" value="4号楼" />
          </el-select>
        </el-form-item>
        <el-form-item label="寝室号" prop="dormNo">
          <el-input v-model="form.dormNo" placeholder="请输入寝室号" />
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input v-model="form.floor" type="number" placeholder="请输入楼层" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男生" value="男" />
            <el-option label="女生" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input v-model="form.capacity" type="number" placeholder="请输入容量" />
        </el-form-item>
        <el-form-item label="房型" prop="roomType">
          <el-select v-model="form.roomType" placeholder="请选择房型">
            <el-option label="四人间" value="四人间" />
            <el-option label="六人间" value="六人间" />
            <el-option label="八人间" value="八人间" />
          </el-select>
        </el-form-item>
        <el-form-item label="设施" prop="facilities">
          <el-input v-model="form.facilities" placeholder="请输入设施（逗号分隔）" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="可用" value="可用" />
            <el-option label="维修中" value="维修中" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'

const searchForm = reactive({
  keyword: '',
  building: '',
  gender: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 30
})

const showAddModal = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  building: '',
  dormNo: '',
  floor: '',
  gender: '',
  capacity: 4,
  currentCount: 0,
  roomType: '四人间',
  facilities: '',
  status: '可用'
})

const rules = {
  building: [{ required: true, message: '请选择楼栋', trigger: 'blur' }],
  dormNo: [{ required: true, message: '请输入寝室号', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入容量', trigger: 'blur' }]
}

const dormList = ref([
  { id: 1, building: '1号楼', dormNo: '101', floor: 1, gender: '男', capacity: 4, currentCount: 3, roomType: '四人间', facilities: '空调,热水器,WiFi', status: '可用' },
  { id: 2, building: '1号楼', dormNo: '102', floor: 1, gender: '男', capacity: 4, currentCount: 4, roomType: '四人间', facilities: '空调,热水器,WiFi', status: '可用' },
  { id: 3, building: '2号楼', dormNo: '203', floor: 2, gender: '女', capacity: 4, currentCount: 2, roomType: '四人间', facilities: '空调,热水器,WiFi', status: '可用' },
  { id: 4, building: '2号楼', dormNo: '302', floor: 3, gender: '女', capacity: 6, currentCount: 6, roomType: '六人间', facilities: '空调,热水器', status: '可用' },
  { id: 5, building: '3号楼', dormNo: '105', floor: 1, gender: '男', capacity: 4, currentCount: 1, roomType: '四人间', facilities: '空调,WiFi', status: '维修中' }
])

const handleSearch = () => {
  pagination.pageNum = 1
  ElMessage.info('搜索功能已触发')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
}

const handleCurrentChange = (page) => {
  pagination.pageNum = page
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showAddModal.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该寝室吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    dormList.value = dormList.value.filter(item => item.id !== id)
    ElMessage.success('删除成功')
  })
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = dormList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      dormList.value[index] = { ...form }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = dormList.value.length > 0 ? Math.max(...dormList.value.map(i => i.id)) + 1 : 1
    dormList.value.push({ ...form, id: newId, currentCount: 0 })
    ElMessage.success('添加成功')
  }
  
  showAddModal.value = false
  resetForm()
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    building: '',
    dormNo: '',
    floor: '',
    gender: '',
    capacity: 4,
    currentCount: 0,
    roomType: '四人间',
    facilities: '',
    status: '可用'
  })
}
</script>

<style lang="scss" scoped>
.dorm-page {
  padding: 20px;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.search-input {
  width: 250px;
}

.search-select {
  width: 150px;
}

.add-btn {
  margin-left: auto;
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

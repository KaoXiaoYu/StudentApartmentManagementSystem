<template>
  <div class="payment-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchForm.keyword" 
        placeholder="请输入学号、姓名或寝室号" 
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
        v-model="searchForm.status" 
        placeholder="缴费状态" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="已缴费" value="已缴费" />
        <el-option label="未缴费" value="未缴费" />
        <el-option label="部分缴费" value="部分缴费" />
      </el-select>
      
      <el-select 
        v-model="searchForm.type" 
        placeholder="费用类型" 
        class="search-select"
      >
        <el-option label="全部" value="" />
        <el-option label="住宿费" value="住宿费" />
        <el-option label="水电费" value="水电费" />
        <el-option label="押金" value="押金" />
        <el-option label="其他" value="其他" />
      </el-select>
      
      <el-button type="primary" class="add-btn" @click="showAddModal = true">
        <el-icon><Plus /></el-icon>
        添加缴费记录
      </el-button>
    </div>
    
    <!-- 表格 -->
    <div class="table-container">
      <el-table :data="paymentList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="dormNo" label="寝室号" width="100" />
        <el-table-column prop="type" label="费用类型" width="100" />
        <el-table-column prop="amount" label="金额(元)" width="100">
          <template #default="scope">
            <span class="amount">{{ scope.row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴(元)" width="100">
          <template #default="scope">
            <span class="amount">{{ scope.row.paidAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="period" label="缴费周期" width="120" />
        <el-table-column prop="deadline" label="截止日期" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === '已缴费' ? 'default' : 'success'" 
              @click="handlePay(scope.row)"
              :disabled="scope.row.status === '已缴费'"
            >
              {{ scope.row.status === '已缴费' ? '已缴费' : '缴费' }}
            </el-button>
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
      :title="isEdit ? '编辑缴费记录' : '添加缴费记录'" 
      :visible.sync="showAddModal"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名" prop="studentName">
          <el-input v-model="form.studentName" placeholder="请输入姓名" />
        </el-form-item>
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
        <el-form-item label="费用类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="住宿费" value="住宿费" />
            <el-option label="水电费" value="水电费" />
            <el-option label="押金" value="押金" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input v-model.number="form.amount" type="number" placeholder="请输入金额" />
        </el-form-item>
        <el-form-item label="缴费周期" prop="period">
          <el-input v-model="form.period" placeholder="如：2024年春季学期" />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker v-model="form.deadline" type="date" placeholder="请选择日期" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddModal = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 缴费弹窗 -->
    <el-dialog title="缴费确认" :visible.sync="showPayModal" width="400px">
      <div class="pay-info">
        <p><strong>学号：</strong>{{ payForm.studentNo }}</p>
        <p><strong>姓名：</strong>{{ payForm.studentName }}</p>
        <p><strong>寝室：</strong>{{ payForm.building }}{{ payForm.dormNo }}</p>
        <p><strong>费用类型：</strong>{{ payForm.type }}</p>
        <p><strong>应缴金额：</strong><span class="pay-amount">¥{{ payForm.amount.toFixed(2) }}</span></p>
        <p><strong>已缴金额：</strong>¥{{ payForm.paidAmount.toFixed(2) }}</p>
        <p><strong>待缴金额：</strong><span class="pay-amount">¥{{ (payForm.amount - payForm.paidAmount).toFixed(2) }}</span></p>
      </div>
      
      <template #footer>
        <el-button @click="showPayModal = false">取消</el-button>
        <el-button type="primary" @click="confirmPay">确认缴费</el-button>
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
  status: '',
  type: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 80
})

const showAddModal = ref(false)
const showPayModal = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: '',
  studentNo: '',
  studentName: '',
  building: '',
  dormNo: '',
  type: '',
  amount: 0,
  paidAmount: 0,
  period: '',
  deadline: '',
  status: '未缴费'
})

const payForm = reactive({
  id: '',
  studentNo: '',
  studentName: '',
  building: '',
  dormNo: '',
  type: '',
  amount: 0,
  paidAmount: 0
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  studentName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  building: [{ required: true, message: '请选择楼栋', trigger: 'blur' }],
  dormNo: [{ required: true, message: '请输入寝室号', trigger: 'blur' }],
  type: [{ required: true, message: '请选择费用类型', trigger: 'blur' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'blur' }]
}

const paymentList = ref([
  { id: 1, studentNo: '2021001', studentName: '张三', building: '1号楼', dormNo: '101', type: '住宿费', amount: 1200, paidAmount: 1200, period: '2024年春季', deadline: '2024-03-31', status: '已缴费' },
  { id: 2, studentNo: '2021002', studentName: '李四', building: '2号楼', dormNo: '203', type: '住宿费', amount: 1200, paidAmount: 600, period: '2024年春季', deadline: '2024-03-31', status: '部分缴费' },
  { id: 3, studentNo: '2022001', studentName: '王五', building: '3号楼', dormNo: '105', type: '水电费', amount: 150, paidAmount: 0, period: '2024年2月', deadline: '2024-03-15', status: '未缴费' },
  { id: 4, studentNo: '2022002', studentName: '赵六', building: '4号楼', dormNo: '302', type: '押金', amount: 500, paidAmount: 500, period: '入学', deadline: '2022-09-01', status: '已缴费' },
  { id: 5, studentNo: '2023001', studentName: '钱七', building: '1号楼', dormNo: '201', type: '住宿费', amount: 1200, paidAmount: 0, period: '2024年春季', deadline: '2024-03-31', status: '未缴费' }
])

const getStatusType = (status) => {
  const types = {
    '已缴费': 'success',
    '部分缴费': 'warning',
    '未缴费': 'danger'
  }
  return types[status] || 'info'
}

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

const handlePay = (row) => {
  Object.assign(payForm, row)
  showPayModal.value = true
}

const confirmPay = () => {
  const index = paymentList.value.findIndex(item => item.id === payForm.id)
  if (index !== -1) {
    paymentList.value[index].paidAmount = paymentList.value[index].amount
    paymentList.value[index].status = '已缴费'
  }
  showPayModal.value = false
  ElMessage.success('缴费成功')
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  if (isEdit.value) {
    const index = paymentList.value.findIndex(item => item.id === form.id)
    if (index !== -1) {
      paymentList.value[index] = { ...form }
    }
    ElMessage.success('编辑成功')
  } else {
    const newId = paymentList.value.length > 0 ? Math.max(...paymentList.value.map(i => i.id)) + 1 : 1
    paymentList.value.push({ ...form, id: newId, paidAmount: 0, status: '未缴费' })
    ElMessage.success('添加成功')
  }
  
  showAddModal.value = false
  resetForm()
}

const resetForm = () => {
  isEdit.value = false
  Object.assign(form, {
    id: '',
    studentNo: '',
    studentName: '',
    building: '',
    dormNo: '',
    type: '',
    amount: 0,
    paidAmount: 0,
    period: '',
    deadline: '',
    status: '未缴费'
  })
}
</script>

<style lang="scss" scoped>
.payment-page {
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

.amount {
  font-weight: 600;
  color: #333;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.pay-info {
  padding: 20px;
}

.pay-amount {
  font-size: 18px;
  font-weight: 700;
  color: #f56c6c;
}
</style>

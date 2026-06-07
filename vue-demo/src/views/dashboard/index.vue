<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon user-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.studentCount }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon building-icon">
          <el-icon :size="28"><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.dormCount }}</div>
          <div class="stat-label">寝室总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon check-icon">
          <el-icon :size="28"><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.checkCount }}</div>
          <div class="stat-label">检查次数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon award-icon">
          <el-icon :size="28"><GobletSquare /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.civilizedCount }}</div>
          <div class="stat-label">文明寝室</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-row">
      <div class="chart-card">
        <h3 class="chart-title">卫生检查统计</h3>
        <div class="chart-placeholder">
          <div class="mini-bar-chart">
            <div class="bar-item">
              <div class="bar" style="height: 80%"></div>
              <span>周一</span>
            </div>
            <div class="bar-item">
              <div class="bar" style="height: 65%"></div>
              <span>周二</span>
            </div>
            <div class="bar-item">
              <div class="bar" style="height: 90%"></div>
              <span>周三</span>
            </div>
            <div class="bar-item">
              <div class="bar" style="height: 70%"></div>
              <span>周四</span>
            </div>
            <div class="bar-item">
              <div class="bar" style="height: 85%"></div>
              <span>周五</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="chart-card">
        <h3 class="chart-title">缴费状态</h3>
        <div class="chart-placeholder">
          <div class="pie-chart">
            <div class="pie-slice paid"></div>
            <div class="pie-slice unpaid"></div>
          </div>
          <div class="pie-legend">
            <span class="legend-item">
              <span class="legend-dot paid-dot"></span>
              <span>已缴费: {{ stats.paidRate }}%</span>
            </span>
            <span class="legend-item">
              <span class="legend-dot unpaid-dot"></span>
              <span>未缴费: {{ 100 - stats.paidRate }}%</span>
            </span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 最近动态 -->
    <div class="activity-card">
      <h3 class="activity-title">最近动态</h3>
      <div class="activity-list">
        <div class="activity-item" v-for="item in recentActivities" :key="item.id">
          <div class="activity-icon" :class="item.type">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="activity-content">
            <p class="activity-text">{{ item.text }}</p>
            <span class="activity-time">{{ item.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, markRaw } from 'vue'
import { User, OfficeBuilding, Check, GobletSquare, Plus, Edit, Delete } from '@element-plus/icons-vue'

const stats = ref({
  studentCount: 1256,
  dormCount: 320,
  checkCount: 89,
  civilizedCount: 23,
  paidRate: 85
})

const recentActivities = ref([
  { id: 1, type: 'add', icon: markRaw(Plus), text: '新增学生: 张三', time: '5分钟前' },
  { id: 2, type: 'edit', icon: markRaw(Edit), text: '修改寝室信息: 3号楼302', time: '15分钟前' },
  { id: 3, type: 'check', icon: markRaw(Check), text: '卫生检查完成: 2号楼', time: '1小时前' },
  { id: 4, type: 'delete', icon: markRaw(Delete), text: '删除学生记录: 李四', time: '2小时前' },
  { id: 5, type: 'add', icon: markRaw(GobletSquare), text: '评选文明寝室: 1号楼101', time: '3小时前' }
])
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.student-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; }
  &.dorm-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: #fff; }
  &.check-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: #fff; }
  &.award-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: #fff; }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mini-bar-chart {
  display: flex;
  align-items: flex-end;
  gap: 24px;
  height: 150px;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar {
  width: 40px;
  background: linear-gradient(180deg, #ff95d1 0%, #a8bfff 100%);
  border-radius: 8px 8px 0 0;
  min-height: 10px;
}

.pie-chart {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: conic-gradient(#4facfe 0deg, #4facfe 306deg, #f093fb 306deg, #f093fb 360deg);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 70px;
    height: 70px;
    background: #fff;
    border-radius: 50%;
  }
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #666;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  
  &.paid-dot { background: #4facfe; }
  &.unpaid-dot { background: #f093fb; }
}

.activity-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.activity-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
}

.activity-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &.add { background: #e8f5e9; color: #4caf50; }
  &.edit { background: #e3f2fd; color: #2196f3; }
  &.delete { background: #ffebee; color: #f44336; }
  &.check { background: #e8eaf6; color: #3f51b5; }
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #333;
  margin: 0;
}

.activity-time {
  font-size: 12px;
  color: #999;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .charts-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>

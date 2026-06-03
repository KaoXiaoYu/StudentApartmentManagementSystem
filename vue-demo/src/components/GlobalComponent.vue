<template>
  <h1 ref="hRef">欢迎来到{{ props.title }}系统</h1><br/>
  <span>{{ props.num }}</span>
  <br/>
  <!-- <button @click="$emit('someEvent', 1, 2)">Click Me</button> -->
  <button @click="handleClick">Click Me</button>
</template>

<script setup>
import { onBeforeMount, onMounted, useTemplateRef, ref } from 'vue';

const emit = defineEmits(['someEvent'])

function handleClick() {
    emit('someEvent', 1, 2)
}

const props = defineProps({
    title: String,
    num: {
        type: Number,
        required: true
    }
})
console.log(props)

const hRef = ref(null)

onBeforeMount(() => {
    console.log('before mount')
    console.log(hRef)
})

onMounted(() => {
    console.log('globalComponent挂载完成')
    console.log(hRef)
})
</script>

<style scoped>
</style>
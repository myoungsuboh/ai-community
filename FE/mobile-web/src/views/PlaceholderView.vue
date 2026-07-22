<script setup>
defineProps({
  title: { type: String, required: true },
  phase: { type: String, default: '' },
})
</script>

<template>
  <v-container class="container-max section">
    <v-row justify="center">
      <v-col cols="12" md="8" class="text-center">
        <p v-if="phase" class="eyebrow text-primary mb-3">{{ phase }}</p>
        <h1 class="display-xl mb-4">{{ title }}</h1>
        <p class="lead text-medium-emphasis mb-8">
          이 화면은 곧 실제 데이터와 연결되어 준비됩니다.
        </p>
        <v-btn size="large" variant="outlined" to="/home">홈으로 돌아가기</v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

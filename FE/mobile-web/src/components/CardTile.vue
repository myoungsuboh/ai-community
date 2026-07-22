<script setup>
import { useRouter } from 'vue-router'
import ScoreRing from './ScoreRing.vue'

defineProps({
  card: { type: Object, required: true },
})
const router = useRouter()
</script>

<template>
  <v-card class="pa-5 h-100 card-hover d-flex flex-column" @click="router.push(`/cards/${card.slug}`)">
    <div class="d-flex justify-space-between align-start mb-3">
      <v-chip size="small" variant="tonal" color="primary">{{ card.category }}</v-chip>
      <ScoreRing :score="card.scoreTotal" :size="56" />
    </div>
    <h3 class="display-md mb-2 text-balance">{{ card.title }}</h3>
    <v-alert
      v-if="!card.sourceAccessible"
      type="warning" variant="tonal" density="compact" class="mb-2 text-caption"
    >
      원본 접근 불가
    </v-alert>
    <v-spacer />
    <div class="d-flex align-center ga-4 mt-3 text-medium-emphasis text-body-2">
      <span><v-icon size="16" icon="mdi-star-outline" /> {{ card.starCount }}</span>
      <span><v-icon size="16" icon="mdi-heart-outline" /> {{ card.likeCount }}</span>
      <span><v-icon size="16" icon="mdi-bookmark-outline" /> {{ card.bookmarkCount }}</span>
      <span><v-icon size="16" icon="mdi-comment-outline" /> {{ card.commentCount }}</span>
    </div>
  </v-card>
</template>

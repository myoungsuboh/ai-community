<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuery } from '@tanstack/vue-query'
import { projectsApi, PROJECT_STATUS } from '@/api/projects'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const keyword = ref('')
const search = ref('')
const { data, isLoading, isError, refetch } = useQuery({
  queryKey: computed(() => ['projects', search.value]),
  queryFn: () => projectsApi.list({ keyword: search.value || undefined, page: 0, size: 20 }),
})
const projects = computed(() => data.value?.content ?? [])

function goNew() {
  if (!auth.isAuthenticated) return router.push({ name: 'login', query: { redirect: '/projects/new' } })
  router.push('/projects/new')
}
function doSearch() {
  search.value = keyword.value.trim()
}
const statusColor = (s) => ({ RECRUITING: 'primary', IN_PROGRESS: 'warning', DONE: 'success' })[s]
</script>

<template>
  <v-container class="container-max section">
    <div class="d-flex flex-wrap justify-space-between align-center mb-6 ga-4">
      <div>
        <p class="eyebrow text-primary mb-2">협업</p>
        <h1 class="display-xl">프로젝트</h1>
      </div>
      <v-btn color="primary" variant="flat" size="large" prepend-icon="mdi-plus" @click="goNew">
        새 프로젝트
      </v-btn>
    </div>

    <v-text-field
      v-model="keyword" placeholder="프로젝트 검색" prepend-inner-icon="mdi-magnify" clearable class="mb-6"
      @keyup.enter="doSearch" @click:clear="doSearch"
    />

    <div v-if="isLoading">
      <v-row><v-col v-for="n in 4" :key="n" cols="12" md="6"><v-skeleton-loader type="article" /></v-col></v-row>
    </div>
    <v-alert v-else-if="isError" type="error" variant="tonal">
      프로젝트를 불러오지 못했어요.
      <template #append><v-btn size="small" variant="text" @click="refetch()">다시 시도</v-btn></template>
    </v-alert>
    <div v-else-if="projects.length === 0" class="text-center py-16">
      <p class="display-md mb-2">아직 프로젝트가 없어요.</p>
      <p class="text-medium-emphasis">첫 프로젝트를 만들어보세요.</p>
    </div>
    <v-row v-else>
      <v-col v-for="p in projects" :key="p.projectId" cols="12" md="6">
        <v-card class="pa-5 h-100 card-hover" @click="router.push(`/projects/${p.projectId}`)">
          <div class="d-flex justify-space-between align-start mb-2">
            <h3 class="display-md">{{ p.name }}</h3>
            <v-chip size="small" :color="statusColor(p.status)" variant="tonal">{{ PROJECT_STATUS[p.status] }}</v-chip>
          </div>
          <div class="d-flex flex-wrap ga-1">
            <v-chip v-for="t in p.tags" :key="t" size="x-small" variant="tonal">#{{ t }}</v-chip>
          </div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

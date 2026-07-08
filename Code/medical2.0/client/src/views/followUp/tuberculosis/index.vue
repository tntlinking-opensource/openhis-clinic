<template>
    <div>
        <iframe :src="followUpUrl" frameborder=0 allowfullscreen style="width: 100%;height: 1000px;"></iframe>
        <meta http-equiv="Content-Security-Policy" content="upgrade-insecure-requests">
    </div>
</template>
<script>
export default {
    computed: {
        followUpUrl() {
            return process.env.FOLLOW_UP_URL + '/s/c1e5183064dc499a8b3ec81f39d9f18e'
        }
    }
}
</script>

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
            return process.env.FOLLOW_UP_URL + '/s/11150b9499e04e74b16f09e0f3b6e0a1'
        }
    }
}
</script>

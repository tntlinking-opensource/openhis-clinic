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
            return process.env.FOLLOW_UP_URL + '/s/9dadd587b567455a9055d7b82a5dff4b'
        }
    }
}
</script>

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
            return process.env.FOLLOW_UP_URL + '/s/8421ad792ba54d7b923c0e3d41b075b0'
        }
    }
}
</script>

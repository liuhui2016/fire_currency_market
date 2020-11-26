<template>
    <div>
       <div v-for="item in list">
         <h1>{{item.name}}</h1>
       </div>
       <button @click="update">aaaa</button>
    </div>
</template>

<script>
  export default {
    data(){
      return{
         list:[
           {
             name:"xiaobin"
           },{
             name:"xiaobin2"
           }
         ]
      }
    },
    mounted() {
    },
    methods:{
      update(){
        this.list[0].name = "aaa";
      }
    }
  }
</script>

<style>
</style>

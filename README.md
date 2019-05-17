# paylibTest

  ####先build 一个PayHelper 对象
  PayHelper payHelper = new PayHelper.Builder()
                .setPayMethod(payWay)
                .setSubject(subject)
                .setBody(body)
                .setTotalAmount(total_fee)
                .setMid(mid)
                .setSeller_id(sellerId)
                .build();

   ####调用doPay 开启支付,回调方法中返回支付结果
    //payHelper.checkPermissions();
    payHelper.doPay(this,new PayResultCallback() {
        @Override
        public void onSuccess(int payMethod) {
            Log.e("pay","onSuccess payMethod:" + payMethod);

        }

        @Override
        public void onError(int payMethod,int errorCode, String errorMsg) {
            Log.e("pay","errorCode="+ errorCode+",errorMsg="+errorMsg);
        }

        @Override
        public void onCancel(int payMethod) {
            Log.e("pay","onCancel");
        }
    });

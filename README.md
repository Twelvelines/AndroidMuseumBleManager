# [English version](README-en.md)

# AndroidBleManager

# 使用
将下面的代码增加到build.gradle文件中,${latest.version} is [![Download][bintray_svg]][bintray_url]
```
dependencies {
    compile 'com.blakequ.androidblemanager:bluetooth-manager-lib:${latest.version}'
}
```
maven
```
<dependency>
  <groupId>com.blakequ.androidblemanager</groupId>
  <artifactId>bluetooth-manager-lib</artifactId>
  <version>${latest.version}</version>
  <type>pom</type>
</dependency>
```



# 使用说明
## 扫描
- 获取扫描管理器
```
BluetoothScanManager scanManager = BleManager.getScanManager(context);
```

- 开始扫描
```
scanManager.addScanFilterCompats(new ScanFilterCompat.Builder().setDeviceName("oby").build());

        scanManager.setScanOverListener(new ScanOverListener() {
                    @Override
                    public void onScanOver() {
                        //scan over of one times
                    }
                });
                
        scanManager.setScanCallbackCompat(new ScanCallbackCompat() {
            @Override
            public void onBatchScanResults(List<ScanResultCompat> results) {
                super.onBatchScanResults(results);
            }
            
            @Override
            public void onScanFailed(final int errorCode) {
                super.onScanFailed(errorCode);
                //code
            }

            @Override
            public void onScanResult(int callbackType, ScanResultCompat result) {
                super.onScanResult(callbackType, result);
                //scan result
            }
        });
        //start scan
        scanManager.startCycleScan(); //不会立即开始，可能会延时
        //scanManager.startScanNow(); //立即开始扫描
```

- 暂停扫描
```
scanManager.stopCycleScan();
//is scanning
scanManager.isScanning()
```

- 资源释放
```
scanManager.release();
```

- 其他
```
getPowerSaver(); //可以参考具体使用方法BackgroundPowerSaver，可以设置循环扫描前台和后台扫描和间隔的时间
startScanOnce(); //单次扫描，只扫描一次
setAPI21ScanningDisabled(false); //禁止使用新的扫描方式
```

- 注意事项
> 扫描结果回调已经放在了主线程，可直接使用并更新视图UI

## 单设备连接
- 获取连接管理器
```
BluetoothConnectManager connectManager = BleManager.getConnectManager(context);
```
- 连接状态监听
```
//如果不用需要移除状态监听removeConnectStateListener
connectManager.addConnectStateListener(new ConnectStateListener() {
            @Override
            public void onConnectStateChanged(String address, ConnectState state) {
                switch (state){
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        break;
                    case NORMAL:
                        break;
                }
            }
        });
connectManager.setBluetoothGattCallback(new BluetoothGattCallback() {
    ...
    //注意：里面的回调方法都是在非主线程
}
```

- 蓝牙读写数据与通知

为了简化蓝牙连接，已经自动封装了蓝牙Gatt的读写和通知。
    - 传统方式
```
private BluetoothGatt mBluetoothGatt;
BluetoothGattCharacteristic characteristic;
boolean enabled;
... 
mBluetoothGatt.setCharacteristicNotification(characteristic, enabled); 
... 
BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE); 
mBluetoothGatt.writeDescriptor(descriptor);
```
    - 封装使用
```
//start subscribe auto
//1.set service uuid(将要读取GattService的UUID)
connectManager.setServiceUUID(serverUUid.toString());
//2.clean history descriptor data（清除历史订阅读写通知）
 connectManager.cleanSubscribeData();
//3.add subscribe params（读写和通知）
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicRead(characteristic.getUuid()).build());//read characteristic
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicNotify(characteristic.getUuid()).build()); //notify
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicWrite(characteristic.getUuid()).build()); //write characteristic
connectManager.addBluetoothSubscribeData(
                        new BluetoothSubScribeData.Builder().setCharacteristicWrite(characteristic.getUuid(), byteData).build();
//还有读写descriptor
//start subscribe(注意，在使用时当回调onServicesDiscovered成功时会自动调用该方法，所以只需要在连接之前完成1,3步即可),如果需要单独读写某些属性，则可以单独调用该方法，并且同样使用步骤2,3然后调用该方法手动启动订阅
boolean isSuccess = connectManager.startSubscribe(gatt); //返回是否成功实现订阅
```

- 连接与断开
```
connect(macAddress);
disconnect(macAddress);
closeAll(); //关闭所有连接设备
getConnectedDevice(); //获取当前已经连接的设备列表
getCurrentState(); //获取当前设备状态
```

- 资源释放
```
scanManager.release();
```

- 注意事项
> 设备的连接，断开尽量在主线程中完成，否则在某些机型（三星）会出现许多意想不到的错误。

## 多设备连接
- 获取多设备连接管理器
```
MultiConnectManager multiConnectManager = BleManager.getMultiConnectManager(context);
```

- 添加状态监听
```
//如果不用需要移除状态监听removeConnectStateListener
connectManager.addConnectStateListener(new ConnectStateListener() {
            @Override
            public void onConnectStateChanged(String address, ConnectState state) {
                switch (state){
                    case CONNECTING:
                        break;
                    case CONNECTED:
                        break;
                    case NORMAL:
                        break;
                }
            }
        });
connectManager.setBluetoothGattCallback(new BluetoothGattCallback() {
    ...
    //注意：里面的回调方法都是在非主线程
}
```

- 添加待设备到队列
如果添加的设备超过了最大连接数，将会自动移除多余的设备
```
addDeviceToQueue(deviceList);
//手动移除多余的连接设备
removeDeviceFromQueue(macAddress);
```

- 蓝牙读写数据与通知
```
//start subscribe auto
//1.set service uuid(将要读取GattService的UUID)
connectManager.setServiceUUID(serverUUid.toString());
//2.clean history descriptor data（清除历史订阅读写通知）
 connectManager.cleanSubscribeData();
//3.add subscribe params（读写和通知）
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicRead(characteristic.getUuid()).build());//read characteristic
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicNotify(characteristic.getUuid()).build()); //notify
connectManager.addBluetoothSubscribeData(
          new BluetoothSubScribeData.Builder().setCharacteristicWrite(characteristic.getUuid()).build()); //write characteristic
connectManager.addBluetoothSubscribeData(
                        new BluetoothSubScribeData.Builder().setCharacteristicWrite(characteristic.getUuid(), byteData).build();
//还有读写descriptor
//start descriptor(注意，在使用时当回调onServicesDiscovered成功时会自动调用该方法，所以只需要在连接之前完成1,3步即可)
connectManager.startSubscribe(gatt);
```

- 开始连接
```
startConnect();
//连接其中的指定设备
startConnect(String);
```

- 资源释放
```
scanManager.release();
```

- 其他
```
getQueueSize(); //当前队列中设备数
setMaxConnectDeviceNum(); //设置最大连接数量
getMaxLen(); //获取最大的连接数量
getConnectedDevice(); //获取已经连接的设备
getDeviceState(macAddress); //获取当前设备连接状态
getAllDevice();
getAllConnectedDevice();
getAllConnectingDevice();
```

## 个性化扫描和连接配置
1. 可设置参数如下：
```
boolean isDebugMode = false; //是否为debug模式，建议使用BuildConfig.DEBUG设置，如果为true则打印日志
long foregroundScanPeriod = 10000; //在前台时（可见扫描界面）扫描持续时间
long foregroundBetweenScanPeriod = 5000; //在前台时（可见扫描界面）扫描间隔暂停时间，我们扫描的方式是间隔扫描
long backgroundScanPeriod = 10000; //在后台时（不可见扫描界面）扫描持续时间
long backgroundBetweenScanPeriod = 5 * 60 * 1000; //在后台时（不可见扫描界面）扫描间隔暂停时间，我们扫描的方式是间隔扫描
int maxConnectDeviceNum = 5;//一次最多连接设备个数
int reconnectStrategy = 3; //如果连接自动断开之后的重连策略（ConnectConfig.RECONNECT_LINEAR，ConnectConfig.RECONNECT_EXPONENT，ConnectConfig.RECONNECT_LINE_EXPONENT,ConnectConfig.RECONNECT_FIXED_TIME）
int reconnectMaxTimes = Integer.MAX_VALUE; //最大重连次数，默认可一直进行重连
long reconnectBaseSpaceTime = 8000; //重连基础时间间隔ms，重连的时间间隔
int reconnectedLineToExponentTimes = 5; //快速重连的次数(线性到指数，只在reconnectStrategy=ConnectConfig.RECONNECT_LINE_EXPONENT时有效)
int connectTimeOutTimes = 15000; //连接超时时间15s,15s后自动检测蓝牙状态（如果设备不在连接范围或蓝牙关闭，则重新连接的时间会很长，或者一直处于连接的状态，现在超时后会自动检测当前状态）
```
2. 使用方法
```
BleManager.setBleParamsOptions(new BleParamsOptions.Builder()
                .setBackgroundBetweenScanPeriod(5 * 60 * 1000)
                .setBackgroundScanPeriod(10000)
                .setForegroundBetweenScanPeriod(5000)
                .setForegroundScanPeriod(10000)
                .setDebugMode(BuildConfig.DEBUG)
                .setMaxConnectDeviceNum(5)
                .setReconnectBaseSpaceTime(8000)
                .setReconnectMaxTimes(Integer.MAX_VALUE)
                .setReconnectStrategy(ConnectConfig.RECONNECT_LINE_EXPONENT)
                .setReconnectedLineToExponentTimes(5)
                .setConnectTimeOutTimes(20000)
                .build());
```

# 权限
使用时需要如下权限
* `android.permission.BLUETOOTH`
* `android.permission.BLUETOOTH_ADMIN`

if SDK >= 23, 增加权限

* `android.permission.ACCESS_COARSE_LOCATION`
* `android.permission.ACCESS_FINE_LOCATION`


# 链接参考
其中ibeacon封装和扫描部分代码参考如下开源库，在此感谢作者的无私奉献。
- [Bluetooth-LE-Library](https://github.com/alt236/Bluetooth-LE-Library---Android)
- [BluetoothCompat](https://github.com/joerogers/BluetoothCompat)


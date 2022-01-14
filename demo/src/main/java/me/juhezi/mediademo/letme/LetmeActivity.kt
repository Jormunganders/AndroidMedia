package me.juhezi.mediademo.letme

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.*
import android.graphics.drawable.AnimatedVectorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_letme.*
import kotlinx.android.synthetic.main.item_device.view.*
import me.juhezi.mediademo.R
import me.juhezi.slow_cut_base.util.CommonUtil.context
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*
import kotlin.math.atan2
import kotlin.math.sqrt


class LetmeActivity : AppCompatActivity() {

    private var mAdapter = BluetoothAdapter.getDefaultAdapter()

    private val mBluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val callback = Callback()

    private val deviceAdapter = DeviceAdapter()

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letme)
        val tts = TextToSpeech(this) {

        }
        findViewById<ImageView>(R.id.image).setOnClickListener {
            ((it as ImageView).drawable as AnimatedVectorDrawable).start()
//            tts.speak("Coroutine", TextToSpeech.QUEUE_FLUSH, null)
            AlertDialog.Builder(this@LetmeActivity)
                .setTitle("HelloWorld")
                .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        mBluetoothAdapter!!.stopLeScan(leScanCallback)
//                        mBluetoothAdapter!!.bluetoothLeScanner.stopScan(callback)
                    }
                })
                .create()
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        window?.setBackgroundBlurRadius(10)
                    }
                }
                .show()
        }

        val intentFilter = IntentFilter()
            .apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                addAction(BluetoothDevice.ACTION_UUID)
            }
        registerReceiver(mReceiver, intentFilter)


        /*android.Manifest.permission.LOCAL_MAC_ADDRESS*/
//        requestPermissions(arrayOf(Manifest.permission.), 0x123)

        self_info.text = "${mAdapter.name} : ${getBluetoothAddressSdk23(mAdapter)}"
        mAdapter.startDiscovery()
        devices_list.layoutManager = LinearLayoutManager(this)
        devices_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        devices_list.adapter = deviceAdapter

        // BLE
//        mBluetoothAdapter!!.startLeScan(leScanCallback)
//        mBluetoothAdapter!!.bluetoothLeScanner.startScan(callback)
//        startAd()

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val x = event?.values?.get(SensorManager.DATA_X) ?: 0f
                val y = event?.values?.get(SensorManager.DATA_Y) ?: 0f
                val z = event?.values?.get(SensorManager.DATA_Z) ?: 0f
                Log.i("Juhezix", "onSensorChanged: [$x, $y, $z] ")
                if (y == 0f) {
                    Log.i("Juhezix", "onSensorChanged: Y == 0 ")
                } else {
                    val radius = atan2(sqrt(x * x + z * z), y)
                    val angle = (radius / Math.PI * 180f).toInt()
                    Log.i("Juhezix", "onSensorChanged: angle: $angle")
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    @SuppressLint("MissingPermission")
    private fun startAd() {
        val settings = AdvertiseSettings.Builder()
            //设置广播模式，以控制广播的功率和延迟。
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            //发射功率级别
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            //不得超过180000毫秒。值为0将禁用时间限制。
            .setTimeout(0)
            //设置是否可以连接
            .setConnectable(true)
            .build()
        // 广播包
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .build()
        // 响应包
        val responseData = AdvertiseData.Builder()
            //隐藏广播设备名称
            .setIncludeDeviceName(false)
            //隐藏发射功率级别
            .setIncludeDeviceName(false)
            //设置广播的服务 UUID
            .addServiceUuid(ParcelUuid(UUID.fromString("0000fff7-0000-1000-8000-00805f9b34fb")))
            // 设置特定的数据
            .addManufacturerData(0x11, byteArrayOf(0x34, 0x56))
            .build()
        mBluetoothAdapter!!.name = "aoligei"
        mBluetoothAdapter!!.bluetoothLeAdvertiser.startAdvertising(
            settings,
            data,
            responseData,
            object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                    super.onStartSuccess(settingsInEffect)
                    Log.i("Juhezi", "onStartSuccess: 广播成功")
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    Log.i("Juhezi", "errorCode: $errorCode")

                }

            }
        )
    }

    @SuppressLint("MissingPermission")
    private val leScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        var message = ""
        scanRecord.forEach {
            message += String.format("%02x ", it)
        }
        Log.i("Juhezi", "device: ${device.address}\t${device.name}\t${scanRecord.size}\n$message")
        /*AdvertiseData.Builder()
            .addServ*/
        var name = UUID.nameUUIDFromBytes(scanRecord).toString()
        Log.i("Juhezi", "Nice: $name")
    }

    class Callback : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
            super.onScanResult(callbackType, scanResult)
            Log.i("Juhezi", "ScanResult:$scanResult");
            //Log.e("TAG","onScanResult :"+utils.Bytes2HexString(scanData));//与旧接口一致
            Log.i(
                "Juhezi",
                "device:" + scanResult.device.address + "  " + scanResult.device.getName()
            );
            Log.i("Juhezi", "scanRecord:" + scanResult.scanRecord.toString());
            Log.i(
                "Juhezi",
                "scanRecord getManufacturerSpecificData:" + (scanResult.scanRecord?.manufacturerSpecificData?.size()
                    ?: "#")
            )
        }

    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        mAdapter.cancelDiscovery()
        mBluetoothAdapter!!.stopLeScan(leScanCallback)
    }

    private val mDeviceList = ArrayList<BluetoothDevice>()

    private val mReceiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    (intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice)?.apply {
                        mDeviceList.add(this)
                        deviceAdapter.addDevice(Device(address, name ?: "NO NAME"))
                    }
                    Log.i("Juhezi", "ACTION_FOUND: " + mDeviceList.size)
                }
                // discovery has finished, give a call to fetchUuidsWithSdp on every device in list.
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Toast.makeText(this@LetmeActivity, "搜索结束", Toast.LENGTH_SHORT).show()
                    if (mDeviceList.isNotEmpty()) {
                        val list = Observable.fromIterable(mDeviceList)
                            .distinct {
                                it.address
                            }.toList().blockingGet()
                        mDeviceList.clear()
                        mDeviceList.addAll(list)
                        Log.i("Juhezi", "List: ${mDeviceList.size}")

                        mDeviceList.forEach {
                            Log.i("Juhezi", "-------------")
                            Log.i("Juhezi", "name: ${it.name}")
                            Log.i("Juhezi", "address: ${it.address}")
                            /*it.uuids?.forEach { uuid ->
                                Log.i("Juhezi", "uuid: $uuid")
                            }*/
                            Log.i("Juhezi", "=============")
                        }
//                        val result = mDeviceList.removeAt(0).fetchUuidsWithSdp()
                    }
                }
                /*BluetoothDevice.ACTION_UUID -> {
                    val device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
                    device?.apply {
                        Log.i("Juhezi", "##################")
                        val uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID)
                        Log.i("Juhezi", "address: $address")
                        uuidExtra?.forEach {
                            Log.i("Juhezi", "uuidExtra: $it")
                        }
                        if (mDeviceList.isNotEmpty()) {
                            val result = mDeviceList.removeAt(0).fetchUuidsWithSdp()
                        }
                        Log.i("Juhezi", "~~~~~~~~~~~~~~~~~~")
                    }
                }*/
            }
        }
    }

    private fun byteArrayToString(ba: ByteArray): String {
        val hex = StringBuilder(ba.size * 2)
        for (b in ba) hex.append("$b ")
        return hex.toString()
    }

}

class DeviceAdapter : RecyclerView.Adapter<DeviceViewHolder>() {

    private val mDeviceList = ArrayList<Device>()
    private var mRecyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(mDeviceList[position])
    }

    override fun getItemCount(): Int = mDeviceList.size

    fun addDevice(device: Device) {
        if (!mDeviceList.contains(device)) {
            mDeviceList.add(device)
            val position = mDeviceList.size - 1
            notifyItemInserted(position)
            mRecyclerView?.scrollToPosition(position)

        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

}

data class Device(val address: String, val name: String) {
    override fun equals(other: Any?): Boolean {
        return TextUtils.equals(address, (other as? Device)?.address)
    }
}

class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(device: Device) {
        itemView.findViewById<TextView>(R.id.name).text = device.name
        itemView.findViewById<TextView>(R.id.address).text = device.address
    }

}

@SuppressLint("MissingPermission")
@TargetApi(23)
fun getBluetoothAddressSdk23(adapter: BluetoothAdapter): String? {
    return try {
        val field = BluetoothAdapter::class.java.getDeclaredField("mService")
        field.isAccessible = true
        val service = field.get(adapter)
        val method = service.javaClass.getMethod("getAddress")
        return method.invoke(service).toString()
    } catch (e: Exception) {
        e.printStackTrace()
        adapter.address
    }
}
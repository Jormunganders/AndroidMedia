package me.juhezi.mediademo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import me.juhezi.mediademo.ui.MediaDemoTheme

class ComposeDemoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaDemoTheme(darkTheme = true) {
                Surface(color = MaterialTheme.colors.background) {
                    TextSample()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TextSample()
}

@Composable
fun TextSample() {
    Card(
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.background
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                vectorResource(id = R.drawable.ic_launcher_foreground),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
            )
            Column(Modifier.preferredSizeIn(minWidth = 360.dp)) {
                Text(
                    text = "Welcome to China!",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.lang.Math.pow
import kotlin.math.sqrt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var points by remember { mutableStateOf(mutableSetOf<Offset>()) }
    var flag by remember { mutableStateOf(false) }
    Canvas(modifier = Modifier.fillMaxSize().clickable {}.onPointerEvent(PointerEventType.Press) {
        var new_point = it.changes.first().position
        if(it.buttons.isPrimaryPressed){
            points.add(new_point)
        }
        else {
            if(!points.isEmpty()) {
                var nearest_point = points.first()
                for (point in points) {
                    if(dist(new_point,point)<dist(new_point, nearest_point)){
                        nearest_point = point
                    }
                }
                if(dist(new_point, nearest_point)<10f){
                    points.remove(nearest_point)
                }
            }
            flag = true
        }
    }) {
        flag = false
        print(flag)
        for (point in points) {
            this.drawCircle(color = Color.Black, radius = 5f, center = point)
        }
        if(points.size>0) {
            var first_point = first_point(points)
            var current_point = Offset(first_point.x+1, first_point.y)
            var old_point = first_point
            while (current_point != first_point) {
                var max = -1f
                var new_point = first_point
                for(point in points) {
                    if(point!=current_point) {
                        var new = cos_of_angle(current_point - old_point, point - current_point)
                        if(new>=max){
                            max = new
                            new_point = point
                        }
                    }
                }
                this.drawLine(Color.Black, start = current_point, end = new_point)
                old_point = current_point
                current_point = new_point
            }
        }
    }
}

fun dist(A: Offset, B: Offset):Float{
    return sqrt((A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y))
}

fun first_point(points: MutableSet<Offset>):Offset{
    var first_point = points.first()
    for (point in points) {
        if(point.x > first_point.x){
            first_point = point
        }
        if(point.x == first_point.x && point.y>first_point.y){
            first_point = point
        }
    }
    return first_point
}

fun cos_of_angle(u: Offset, v: Offset):Float{
    return scalar(u,v)/(len(u)*len(v))
}
fun scalar(u: Offset, v: Offset):Float{
    return u.x*v.x+u.y*v.y
}
fun len(v: Offset):Float{
    return sqrt(v.x*v.x+v.y*v.y)
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

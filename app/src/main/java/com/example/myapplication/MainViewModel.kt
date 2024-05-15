import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.CompassRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    private val repository: CompassRepository,
) : ViewModel() {

    private val every10thCharacterMutableLiveData = MutableLiveData<String?>()
    val every10thCharacter: MutableLiveData<String?> = every10thCharacterMutableLiveData

    private val wordCounterMutableLiveData = MutableLiveData<Map<String, Int>>()
    val wordCounter: LiveData<Map<String, Int>> = wordCounterMutableLiveData

    private val isLoadingMutableLiveData = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = isLoadingMutableLiveData

    private fun every10thCharacterRequest(content: String): String {
        return content.filterIndexed { index, _ -> (index + 1) % 10 == 0 }
    }

    private fun wordCounterRequest(content: String): Map<String, Int> {
        return content.split(Regex("\\s+")).groupingBy { it.lowercase() }.eachCount()
    }

    fun getAboutContent() {
        CoroutineScope(Dispatchers.IO).launch {
            isLoadingMutableLiveData.postValue(true)

            val url = "https://www.compass.com/about"

            val deferredContent1 = async { repository.getTextFromUrl(url) }
            val deferredContent2 = async { repository.getTextFromUrl(url) }

            try {
                val content1 = deferredContent1.await()
                val content2 = deferredContent2.await()

                val deferredEvery10thCharacter = async { every10thCharacterRequest(content1) }
                val deferredWordCounter = async { wordCounterRequest(content2) }

                val every10thCharacterResult = deferredEvery10thCharacter.await()
                val wordCounterResult = deferredWordCounter.await()

                withContext(Dispatchers.Main) {
                    every10thCharacterMutableLiveData.value = every10thCharacterResult
                    wordCounterMutableLiveData.value = wordCounterResult
                }
                every10thCharacter.postValue(every10thCharacterResult)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    every10thCharacterMutableLiveData.value = String()
                    wordCounterMutableLiveData.value = emptyMap()
                }
            } finally {
                isLoadingMutableLiveData.postValue(false)
            }
        }
    }
}

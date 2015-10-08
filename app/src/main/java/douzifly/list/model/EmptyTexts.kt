package douzifly.list.model

import douzifly.list.utils.logd
import java.util.*

/**
 * Created by liuxiaoyuan on 2015/10/7.
 */

private val emptyTexts = arrayOf(
    "No things is good things.",
    "Everything is done.",
    "Genius only means hard-working all one’s life.",
    "Reading makes a full man, conference a ready man, and writing an exact man.",
    "And those who were seen dancing were thought to be insane by those who could not hear the music.",
    "The only limit to our realization of tomorrow will be our doubts of today.	",
    "There is no doubt that good things will come, and when it comes, it will be a surprise. ",
    "Reality is merely an illusion, albeit a very persistent one.",
    "The first and greatest victory is to conquer yourself; to be conquered by yourself is of all things most " +
        "shameful and vile.",
    "A pessimist sees the difficulty in every opportunity; an optimist sees the opportunity in every difficulty.",
    "There is nothing noble in being superior to some other man. The true nobility is in being superior to your " +
        "previous self. ",
    "A man is not old as long as he is seeking something. A man is not old until regrets take the place of dreams.",
    "I was not looking for my dreams to interpret my life, but rather for my life to interpret my dreams. "
)

fun randomEmptyText(): String {
  val n = Random().nextInt(emptyTexts.size())
  "next ${n}".logd("ListApp")
  return emptyTexts[n]
}

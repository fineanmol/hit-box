/*
 * This file is part of Gravity Box.
 *
 * Gravity Box is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gravity Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gravity Box.  If not, see <https://www.gnu.org/licenses/>.
 */

package ro.luca1152.gravitybox.utils.ads

abstract class AdsController {
    // Interstitial ad
    abstract fun showInterstitialAd()
    abstract fun isInterstitialAdLoaded(): Boolean

    // Rewarded ad
    /** True when rewardedVideoAd.show() was called (in AndroidLauncher), but an ad wasn't yet loaded. */
    var isShowingRewardedAdScheduled = false
    var rewardedAdEventListener: RewardedAdEventListener? = null
    abstract fun loadRewardedAd()
    abstract fun showRewardedAd()
    abstract fun isRewardedAdLoaded(): Boolean

    // Network
    abstract fun isNetworkConnected(): Boolean
}
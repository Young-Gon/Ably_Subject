@file:JvmName("DatabindingDelegation")
package com.gondev.ably.subject.util

import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Use setContentView before using the binding variable.
 * ``` kotlin
 * class DataBindingActivity : FragmentActivity() {
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingActivityBinding by dataBinding()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.layout.data_binding_activity)
 *         // You can use binding
 *     }
 * }
 * ```
 *
 * Use Activity's secondary constructor passing layout res id.
 * ``` kotlin
 * class DataBindingActivity : AppCompatActivity(R.layout.data_binding_activity) {
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingActivityBinding by dataBinding()
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 */
fun <T : ViewDataBinding> FragmentActivity.dataBinding() = DataBindingLazy<T>(this)

class DataBindingLazy<T : ViewDataBinding>(
    private val activity: FragmentActivity
): Lazy<T> {
    private var binding: T? = null
    override fun isInitialized(): Boolean = binding != null
    override val value: T
        get() = binding ?: bind<T>(activity.getContentView()).also {
            it.lifecycleOwner = activity
            binding = it
        }

    private fun FragmentActivity.getContentView(): View {
        return checkNotNull(findViewById<ViewGroup>(android.R.id.content).getChildAt(0)) {
            "Call setContentView or Use Activity's secondary constructor passing layout res id."
        }
    }
}

/**
 * Use inflater.inflate in onCreateView.
 * ``` kotlin
 * class DataBindingFragment : Fragment() {
 *
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingFragmentBinding by dataBinding()
 *
 *     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *         return inflater.inflate(R.layout.data_binding_fragment, container, false)
 *     }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 * Use Fragment's secondary constructor passing layout res id.
 * ``` kotlin
 * class DataBindingFragment : Fragment(R.layout.data_binding_fragment) {
 *
 *     // Declare the `binding` variable using `by dataBinding()`.
 *     private val binding: DataBindingFragmentBinding by dataBinding()
 *
 *     // DO NOT override onCreateView
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         // You can use binding
 *     }
 * }
 * ```
 */
fun <T : ViewDataBinding> Fragment.dataBinding() = DataBindingLazyFragment<T>(this)

class DataBindingLazyFragment<T : ViewDataBinding>(
    private val fragment: Fragment
): Lazy<T>{
    private var binding: T? = null
    override val value: T
        get() = binding ?: bind<T>(fragment.requireView()).also {
            it.lifecycleOwner = fragment.viewLifecycleOwner
            binding = it
        }

    override fun isInitialized() = binding != null
}

private fun <T : ViewDataBinding> bind(view: View): T = DataBindingUtil.bind(view)!!
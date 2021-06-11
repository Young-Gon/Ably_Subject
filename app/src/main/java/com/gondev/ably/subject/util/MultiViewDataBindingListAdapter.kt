package com.gondev.ably.subject.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kotlin.reflect.KClass

private class MultiViewDataBindingListAdapter<ItemType : Any>(
    private val lifecycleOwner: LifecycleOwner,
    diffUtil: DiffUtil.ItemCallback<ItemType>,
    private val subclasses: Map<KClass<out ItemType>, SubclassAdapterItem>,
) : PagingDataAdapter<ItemType, RecyclerViewHolder<ItemType>>(diffUtil) {

    override fun getItemViewType(position: Int) =
        checkNotNull(subclasses[getItem(position)!!::class]) {
            "${getItem(position)!!::class}를 처리할 클레스를 등록해 주세요"
        }.itemRes

    override fun onCreateViewHolder(
        parent: ViewGroup,
        layoutResId: Int
    ): RecyclerViewHolder<ItemType> {
        var bindingVariableId: Int? = null
        return RecyclerViewHolder(
            (DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                layoutResId,
                parent,
                false
            ) as ViewDataBinding).apply {
                subclasses.values.find { it.itemRes == layoutResId }?.also {
                    bindingVariableId = it.bindingVariableId
                    it.init?.invoke(this)
                }
                lifecycleOwner = this@MultiViewDataBindingListAdapter.lifecycleOwner
            }, bindingVariableId
        )
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder<ItemType>, position: Int) =
        holder.onBindViewHolder(getItem(position))
}

class SubTypeRegister<BaseType : Any> internal constructor() {
    val subclasses: MutableMap<KClass<out BaseType>, SubclassAdapterItem> = mutableMapOf()

    /**
     * # subType
     * [MultiViewDataBindingListAdapter]에서 하위 항목을 추가할 때 사용합니다
     * @param ItemType 추가할 아이템 타입 (BaseType의 하위 타입)
     * @param Binding 해당 아이템 타입을 사용할 [ViewDataBinding]의 하위 타입
     * @param itemRes 아이템 레이아웃 리소스 ID (R.layout.xxx)
     * @param bindingVariable 레이아웃에서 아이템을 받을 변수의 BindingResource ID (BR.xxx)
     * @param init 아이템 데이터바인딩 초기화 블럭
     */
    @Suppress("UNCHECKED_CAST")
    inline fun <reified ItemType : BaseType, Binding : ViewDataBinding> addSubType(
        @LayoutRes itemRes: Int,
        bindingVariable: Int? = null,
        noinline init: (Binding.() -> Unit)? = null,
    ) {
        subclasses[ItemType::class] = SubclassAdapterItem(
            itemRes,
            bindingVariable,
            init as (ViewDataBinding.() -> Unit)?
        )
    }

    fun buildAdapter(lifecycleOwner: LifecycleOwner, diffUtil: DiffUtil.ItemCallback<BaseType>) :PagingDataAdapter<BaseType, RecyclerViewHolder<BaseType>> =
        MultiViewDataBindingListAdapter(lifecycleOwner, diffUtil, subclasses)
}

class SubclassAdapterItem(
    @LayoutRes val itemRes: Int,
    val bindingVariableId: Int? = null,
    val init: (ViewDataBinding.() -> Unit)? = null
)

/**
 * # MultiViewDataBindingListAdapter
 * [RecyclerView][androidx.recyclerview.widget.RecyclerView]에 사용할 수 있는
 * [adapter][androidx.recyclerview.widget.RecyclerView.Adapter]입니다
 * [ListAdapter][androidx.recyclerview.widget.ListAdapter]를 상속 받고 있어,
 * 아이템 갱신시 자동으로 화면이 갱신 됩니다
 * 이름에서도 알수 있듯이 여라타입의 아이템을 추가할 수 있습니다
 * 추가한 아이템은 데이터바인딩을 이용하여 아이템 레이아웃과 바인딩 됩니다
 * 자세한 사용법은 [DataBindingListAdapter]와 동일 합니다
 *
 * * 사용법
 * (1). 아래와 같은 구조의 아이템이 있으면
 * ``` kotlin
 *  // 슈퍼 타입
 *   sealed class SuperType{
 *       abstract val id: Int
 *   }
 *
 *   // 서브 타입 1
 *   data class SubTypeMe(
 *       override val id: Int,
 *       val myName: String,
 *   ): SuperType()
 *
 *   // 서브 타입 2
 *   data class SubTypeMyAsset(
 *       override val id: Int,
 *       val myAsset: String,
 *   ): SuperType()
 *
 *   // 서브 타입 3
 *   data class SubTypeMyFamily(
 *       override val id: Int,
 *       val myFamily: String,
 *   ): SuperType()
 * ```
 * (2). 다음과 같이 쓸 수 있습니다
 * ``` kotlin
 * binding.recyclerview.adapter = MultiViewDataBindingListAdapter(
 *       viewLifecycleOwner,  // 데이터 변화를 관찰할 lifecycleOwner
 *       object : DiffUtil.ItemCallback<SuperType>() {  // 아이템 비교를 위한 DiffUtil
 *       override fun areItemsTheSame(oldItem: SuperType, newItem: SuperType) =
 *           oldItem.id == newItem.id
 *
 *       override fun areContentsTheSame(oldItem: SuperType, newItem: SuperType) =
 *           oldItem.equals(newItem)  // data class가 아니기 때문에 '==' 연산자는 사용할 수 없다
 *
 *       }) {  // 이 블럭에서 하위 타입을 추가할 수 있다
 *           // subType()함수의 자세한 사용법은 해당 함수 comment를 참고 할것.
 *           subType<SubTypeMe,SubTypeMeItemBinding>(R.layout.item_subtype_me, BR.item){
 *               // 이블럭에서 SubTypeMe 아이템 데이터 바인딩
 *               vm=viewModel
 *           }
 *
 *           subType<SubTypeMyAsset,SubTypeMyAssetItemBinding>(R.layout.item_subtype_my_asset, BR.item){
 *               // 이블럭에서 SubTypeMyAsset 아이템 데이터 바인딩
 *               vm=viewModel
 *           }
 *
 *           subType<SubTypeMyFamily,SubTypeMyFamilyItemBinding>(R.layout.item_subtype_my_family, BR.item){
 *               // 이블럭에서 SubTypeMyFamily 아이템 데이터 바인딩
 *               vm=viewModel
 *           }
 *       }
 * )
 * ```
 * (3). 리사이클러뷰 아이템 'item_subtype_me.xml'의 레이아웃은 다음과 같이 구성 되어 있습니다
 * ``` xml
 * <layout ...>
 *     <data class="SubTypeMeItemBinding>
 *         <variable
 *             name="vm"
 *             type="your.viewmodel.package.here"/>
 *     </data>
 *     <!-- 리사이클러뷰의 아이템을 데이터바인딩을 사용하여 여기서 레이아웃 구현 -->
 * </layout>
 * ```
 * (4). 데이터 구성
 * ``` kotlin
 * class MyViewModel: ViewModel(){
 *     // SuperType의 subType으로 구성 되어 있는 리스트 구성
 *     val myList: LiveData<List<SuperType>> = getMyList()
 * }
 * ```
 * (5). 리사이클러뷰에 데이터 추가
 * ``` xml
 * <androidx.recyclerview.widget.RecyclerView
 *     android:id="@+id/recyclerview"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:items="@{vm.myList}"
 *     app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"/>
 * ```
 * @param BaseType 추가할 아이템들이 공통으로 상속받는 SuperType
 * @param lifecycleOwner 데이터바인딩에 사용할 [LifecycleOwner]
 * @param diffUtil 아이템이 갱신될때 사용할 [DiffUtil][DiffUtil.ItemCallback]
 * @param action 하위 타입을 추가할 수 있는 블럭
 * @return [ListAdapter]
 * @see DataBindingListAdapter
 * @see RecyclerViewHolder
 */
fun <BaseType : Any> MultiViewDataBindingListAdapter(
    lifecycleOwner: LifecycleOwner,
    diffUtil: DiffUtil.ItemCallback<BaseType>,
    action: SubTypeRegister<BaseType>.() -> Unit,
):PagingDataAdapter<BaseType, RecyclerViewHolder<BaseType>> {
    val register = SubTypeRegister<BaseType>()
    register.action()
    return register.buildAdapter(lifecycleOwner, diffUtil)
}
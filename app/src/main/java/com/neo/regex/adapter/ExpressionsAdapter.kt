package com.neo.regex.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.neo.highlight.core.Highlight
import com.neo.highlight.core.Scheme
import com.neo.regex.R
import com.neo.regex.databinding.ItemExpressionBinding
import com.neo.regex.model.Expression
import com.neo.regex.utils.genColor
import com.neo.regex.utils.genHSV
import com.neo.utilskt.color
import com.neo.utilskt.dp
import java.util.regex.Pattern

class ExpressionsAdapter : RecyclerView.Adapter<ExpressionsAdapter.Holder>() {

    private var expressions: MutableList<Expression> = mutableListOf()
    private val highlight by lazy { Highlight() }

    private var moreExpressionListener: (() -> Unit)? = null
    private var removeExpressionListener: ((Int) -> Unit)? = null
    private var onMatchListener: ((List<Expression>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemExpressionBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val expression = expressions[position]
        val isLastItem = position == itemCount - 1
        val isMultiField = itemCount > 1
        val context = holder.itemView.context
        val theme = context.theme

        holder.clearListeners()

        holder.moreExpressionBtnIsVisible(isLastItem)

        holder.setExpressionHighlight(highlight)

        holder.setExpression(expression.regex)

        val genRegex = {

            try {
                val regex = expression.regex
                expression.pattern = if (regex.isNotEmpty()) Pattern.compile(regex) else null

                holder.setColor(
                    if (isMultiField) {
                        if (position == 0) {
                            theme.color(R.attr.colorPrimary)
                        } else {
                            genColor(genHSV(position * 15, 250, true))
                        }
                    } else {
                        theme.color(R.attr.colorPrimary)
                    }
                )

            } catch (e: Exception) {
                expression.pattern = null
                holder.showError(e.message)
            }
        }

        genRegex.invoke()

        holder.setExpressionChangeListener {
            expression.regex = it
            genRegex.invoke()
            onMatchListener?.invoke(expressions)
        }

        if (isLastItem) {
            holder.setMoreExpressionListener {
                expressions.add(Expression())

                notifyItemInserted(itemCount)
                notifyItemChanged(position)

                onMatchListener?.invoke(expressions)
            }
        } else {
            holder.setMoreExpressionListener(null)
        }

    }

    override fun getItemCount(): Int {
        return expressions.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAllExpressions(expressions: MutableList<Expression>) {
        this.expressions = expressions
        notifyDataSetChanged()
        onMatchListener?.invoke(expressions)
    }

    fun setMoreExpressionListener(listener: () -> Unit) {
        moreExpressionListener = listener
    }

    fun setRemoveExpressionListener(listener: (Int) -> Unit) {
        removeExpressionListener = listener
    }

    fun attachOnRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = this
        ItemTouchHelper(ExpressionSwipe()).attachToRecyclerView(recyclerView)
    }

    fun setExpressionHighlight(vararg schemes: Scheme) {
        highlight.schemes = schemes.toList()
    }

    fun seOnMatchListener(listener: (List<Expression>) -> Unit) {
        onMatchListener = listener
    }

    class Holder(
        private val binding: ItemExpressionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context = itemView.context
        private val gradientDrawable = binding.llRegexContainer.background as GradientDrawable

        private var highlight: Highlight? = null
        private var expressionChangeListener: ((String) -> Unit)? = null
        private var moreExpressionListener: (() -> Unit)? = null

        init {
            configView()
        }

        private fun configView() {
            itemView.layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val vertical = context.dp(4)
                val horizontal = context.dp(16)
                setMargins(horizontal, vertical, horizontal, vertical)
            }

            binding.etExpression.addTextChangedListener {
                expressionChangeListener?.invoke(it?.toString() ?: "")
                highlight?.apply {
                    removeSpan(it)
                    setSpan(it)
                }
            }

            binding.ibAddExpressionBtn.setOnClickListener {
                moreExpressionListener?.invoke()
            }

        }

        fun moreExpressionBtnIsVisible(isVisible: Boolean) {
            val paddingDefault = context.dp(14)
            binding.ibAddExpressionBtn.visibility = if (isVisible) {
                binding.etExpression.setPadding(
                    paddingDefault,
                    paddingDefault,
                    0,
                    paddingDefault
                )
                View.VISIBLE
            } else {
                binding.etExpression.setPadding(paddingDefault)
                View.GONE
            }
        }

        fun setMoreExpressionListener(listener: (() -> Unit)?) {
            moreExpressionListener = listener
        }

        fun setExpressionHighlight(highlight: Highlight) {
            this.highlight = highlight
        }

        fun setExpressionChangeListener(listener: ((String) -> Unit)) {
            expressionChangeListener = listener
        }

        fun setExpression(regex: String) {
            binding.etExpression.setText(regex)
        }

        fun clearListeners() {
            expressionChangeListener = null
            moreExpressionListener = null
        }

        fun showError(message: String?) {
            gradientDrawable.setStroke(context.dp(1.5f).toInt(), Color.RED)
            binding.etExpression.error = message
        }

        fun setColor(color: Int) {
            gradientDrawable.setStroke(
                context.dp(1.5f).toInt(),
                color
            )

            binding.ibAddExpressionBtn.setColorFilter(color)
            binding.etExpression.error = null
        }
    }

    inner class ExpressionSwipe : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ) = if (itemCount != 1) makeFlag(ACTION_STATE_SWIPE, RIGHT) else 0

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false

        @SuppressLint("NotifyDataSetChanged")
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (direction == RIGHT) {

                expressions.removeAt(viewHolder.adapterPosition)
                notifyItemRemoved(viewHolder.adapterPosition)

                notifyDataSetChanged()

                onMatchListener?.invoke(expressions)

                if (viewHolder is Holder) {
                    viewHolder.clearListeners()
                }
            }
        }
    }
}
